# JasperReports CSV → PDF Demo

## Overview
This project demonstrates how to:
- Design reports using Jaspersoft Studio (.jrxml)
- Load CSV data via Java
- Pass parameters (e.g., logo image)
- Export report to PDF

---

## How to Run
1. Build project (Maven)
2. Run:
   RunReport.java

3. Output:
   C:/jasperResults/KH_HouseSales_11501.pdf

---

## Open in Jaspersoft Studio

This is a Maven/IntelliJ project (NOT Eclipse project)

### Method 1 (Recommended)
File → Open File  
→ src/main/resources/reports/KH_HouseSales_11501.jrxml

### Method 2
File → Import → General → File System  
→ Select project root

### Method 3 (Advanced)
File → Import → Maven → Existing Maven Projects

---

## Project Structure

src/main/resources/
- reports/  → jrxml files
- data/     → CSV data
- images/   → logo/image files
- fonts/    → font files
- jasperreports_extension.properties

---

## Logo Handling (Important)

### jrxml
<imageExpression>
$P{LOGO_STREAM} != null ? $P{LOGO_STREAM} : $P{LOGO_PATH}
</imageExpression>

### Java (runtime)
params.put("LOGO_STREAM", classpath image stream)

---

## Image Path for Studio Preview

Jaspersoft Studio does NOT always load classpath resources  
→ You must set a local fallback path

### Example
LOGO_PATH:
C:/your_project_path/src/main/resources/images/Emblem_of_Kaohsiung_City.png

⚠️ You must change this to your local machine path

---

## Behavior

| Environment       | Image Source   |
|------------------|---------------|
| Java runtime     | LOGO_STREAM   |
| Studio Preview   | LOGO_PATH     |

---

## Notes

- CSV must be UTF-8
- First row must be header
- Field names must match jrxml fields
- Fonts must be configured for Chinese support (if needed)

---

## Key Concept

Studio uses local path  
Java uses classpath  
→ One jrxml works in both environments