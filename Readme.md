# JasperReports CSV → PDF Demo

## Run
- Run `RunReport.java`
- Output: C:/jasperResults/KH_HouseSales_11501.pdf

## Open in Jaspersoft Studio
- File → Open File → reports/KH_HouseSales_11501.jrxml
- 或 File → Import → General → File System

## Logo 設定
jrxml:
<imageExpression>
$P{LOGO_STREAM} != null ? $P{LOGO_STREAM} : $P{LOGO_PATH}
</imageExpression>

Java:
params.put("LOGO_STREAM", classpath image stream)

## 修改圖片路徑（Studio Preview 用）
LOGO_PATH 要改成本機實際路徑，例如：
C:/your_path/src/main/resources/images/Emblem_of_Kaohsiung_City.png

## 結構
src/main/resources/
- reports/
- data/
- images/
- fonts/

## 重點
Studio 用 LOGO_PATH  
Java 用 LOGO_STREAM  
同一份 jrxml 共用