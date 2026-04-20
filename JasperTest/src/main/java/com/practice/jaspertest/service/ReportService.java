package com.practice.jaspertest.service;

import com.practice.jaspertest.model.HouseData;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ReportService {

    // 必要欄位
    private static final List<String> REQUIRED_COLUMNS =
            List.of("district", "itemCount", "landCount", "buildingCount");

    public byte[] generatePdf(MultipartFile file) throws Exception {
        validateFile(file);

        List<HouseData> dataList = parseCsv(file);

        InputStream jrxml = getClass().getClassLoader()
                .getResourceAsStream("reports/KH_HouseSales_11501.jrxml");
        if (jrxml == null) {
            throw new RuntimeException("jrxml not found: reports/KH_HouseSales_11501.jrxml");
        }

        JasperReport report = JasperCompileManager.compileReport(jrxml);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

        Map<String, Object> params = new HashMap<>();

        InputStream logo = getClass().getClassLoader()
                .getResourceAsStream("images/Emblem_of_Kaohsiung_City.png");
        params.put("LOGO_STREAM", logo);

        JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(print, out);
        return out.toByteArray();
    }

    // 解析 CSV
    private List<HouseData> parseCsv(MultipartFile file) throws Exception {
        List<HouseData> list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                throw new RuntimeException("CSV is empty");
            }

            String delimiter = detectDelimiter(headerLine);
            String[] headers = headerLine.split(Pattern.quote(delimiter), -1);
            Map<String, Integer> indexMap = buildHeaderIndexMap(headers);

            validateRequiredColumns(indexMap);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(Pattern.quote(delimiter), -1);
                HouseData data = mapRow(parts, indexMap);

                if (isMeaninglessRow(data)) {
                    continue;
                }

                list.add(data);
            }
        }

        if (list.isEmpty()) {
            throw new RuntimeException("CSV has no valid data rows");
        }

        return list;
    }

    // 建 header 對照表
    private Map<String, Integer> buildHeaderIndexMap(String[] headers) {
        Map<String, Integer> indexMap = new HashMap<>();

        for (int i = 0; i < headers.length; i++) {
            String normalized = normalizeHeader(headers[i]);
            if (!normalized.isEmpty()) {
                indexMap.put(normalized, i);
            }
        }

        return indexMap;
    }

    // 檢查必要欄位
    private void validateRequiredColumns(Map<String, Integer> indexMap) {
        for (String required : REQUIRED_COLUMNS) {
            if (!indexMap.containsKey(required)) {
                throw new RuntimeException(
                        "Missing column: " + required + ", actual headers = " + indexMap.keySet()
                );
            }
        }
    }

    // 對應一列資料
    private HouseData mapRow(String[] parts, Map<String, Integer> indexMap) {
        HouseData data = new HouseData();

        data.setDistrict(cleanText(getValue(parts, indexMap.get("district"))));
        data.setItemCount(cleanInteger(getValue(parts, indexMap.get("itemCount"))));
        data.setLandCount(cleanInteger(getValue(parts, indexMap.get("landCount"))));
        data.setBuildingCount(cleanInteger(getValue(parts, indexMap.get("buildingCount"))));

        return data;
    }

    // 無效列略過
    private boolean isMeaninglessRow(HouseData data) {
        boolean districtEmpty = data.getDistrict() == null || data.getDistrict().isBlank();
        boolean allCountsZero =
                safeInt(data.getItemCount()) == 0 &&
                        safeInt(data.getLandCount()) == 0 &&
                        safeInt(data.getBuildingCount()) == 0;

        return districtEmpty && allCountsZero;
    }

    // null 轉 0
    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    // 安全取值
    private String getValue(String[] parts, Integer index) {
        if (index == null || index < 0 || index >= parts.length) {
            return "";
        }
        return parts[index];
    }

    // header 正規化
    private String normalizeHeader(String header) {
        if (header == null) {
            return "";
        }

        String h = header
                .replace("\uFEFF", "")
                .trim()
                .replace(" ", "")
                .replace("　", "");

        return switch (h) {
            case "區名", "行政區", "區別", "district" -> "district";
            case "件數", "交易件數", "itemCount" -> "itemCount";
            case "土地筆數", "土地數", "landCount" -> "landCount";
            case "建物棟數", "建物數", "buildingCount" -> "buildingCount";
            default -> h;
        };
    }

    // 清理文字
    private String cleanText(String value) {
        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replace("\uFEFF", "")
                .replaceAll("[\\t\\r\\n]+", " ")
                .replaceAll("\\s{2,}", " ");
    }

    // 清理數字，去掉 .00 與雜訊
    private Integer cleanInteger(String value) {
        if (value == null) {
            return 0;
        }

        String cleaned = value.trim();

        // 保留數字、小數點、負號
        cleaned = cleaned.replaceAll("[^0-9.\\-]", "");

        if (cleaned.isBlank() || "-".equals(cleaned) || ".".equals(cleaned) || "-.".equals(cleaned)) {
            return 0;
        }

        try {
            // 100.00 -> 100
            double d = Double.parseDouble(cleaned);
            return (int) d;
        } catch (Exception e) {
            return 0;
        }
    }

    // 偵測分隔符
    private String detectDelimiter(String headerLine) {
        if (headerLine.contains(";")) {
            return ";";
        }
        return ",";
    }

    // 檢查檔案
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            throw new RuntimeException("Only CSV files are allowed");
        }
    }
}