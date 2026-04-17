package org.jaspertest;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RunReport {

    public static void main(String[] args) {
        String outputDir = "C:/jasperResults";
        String outputFile = outputDir + "/KH_HouseSales_11501.pdf";

        try (
                InputStream jrxmlStream = RunReport.class.getClassLoader()
                        .getResourceAsStream("reports/KH_HouseSales_11501.jrxml");
                InputStream csvStream = RunReport.class.getClassLoader()
                        .getResourceAsStream("data/KH_HouseSales_11501.csv");
                InputStream logoStream = RunReport.class.getClassLoader()
                        .getResourceAsStream("images/Emblem_of_Kaohsiung_City.png")
        ) {
            if (jrxmlStream == null) {
                throw new RuntimeException("找不到 jrxml：reports/KH_HouseSales_11501.jrxml");
            }
            if (csvStream == null) {
                throw new RuntimeException("找不到 csv：data/KH_HouseSales_11501.csv");
            }
            if (logoStream == null) {
                throw new RuntimeException("找不到圖片：images/Emblem_of_Kaohsiung_City.png");
            }

            JasperReport report = JasperCompileManager.compileReport(jrxmlStream);

            JRCsvDataSource dataSource = new JRCsvDataSource(csvStream, StandardCharsets.UTF_8.name());
            dataSource.setUseFirstRowAsHeader(true);
            dataSource.setFieldDelimiter(',');

            // 如果 CSV 在 Windows 匯出後讀不到資料，可改成 "\r\n"
            // dataSource.setRecordDelimiter("\r\n");

            Map<String, Object> params = new HashMap<>();
            params.put("LOGO_STREAM", logoStream);

            JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);

            File dir = new File(outputDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("無法建立輸出資料夾：" + outputDir);
            }

            JasperExportManager.exportReportToPdfFile(print, outputFile);
            System.out.println("成功產生 PDF: " + outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}