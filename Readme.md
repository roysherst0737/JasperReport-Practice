# JasperReports CSV → PDF Demo

## Overview
Full-stack demo:
CSV → Spring Boot → Jasper → PDF  
Vue frontend for upload & download

---

## Features

### Backend
- CSV upload
- Flexible parsing (order / alias / BOM)
- Data cleaning:
  - 100.00 → 100
  - $100 / 100筆 → 100
  - empty → 0
- Skip invalid rows
- Bean DataSource
- PDF export

### Frontend
- Upload CSV
- Call API
- Auto download PDF

### Jasper
- .jrxml template
- Bean binding
- Chinese support
- Logo parameter

---

## Run

### Backend
```
cd backend
mvn spring-boot:run
```

### Frontend
```
cd frontend
npm install
npm run dev
```

Frontend:
```
http://localhost:5173
```

---

## Usage
1. Open frontend
2. Upload CSV
3. Click button
4. Download PDF

---

## CSV

Required fields (flexible):

| Field | Names |
|------|------|
| district | 區名 / 行政區 |
| itemCount | 件數 / 交易件數 |
| landCount | 土地筆數 |
| buildingCount | 建物棟數 |

Example:
```
區名,件數,土地筆數,建物棟數
三民區,100.00,200,50
左營區,$80,150筆,40棟
```

---

## Logo

jrxml:
```
$P{LOGO_STREAM} != null ? $P{LOGO_STREAM} : $P{LOGO_PATH}
```

Runtime:
- uses LOGO_STREAM

Studio:
- must set local path:
```
C:/your_project_path/.../images/xxx.png
```

---

## Notes
- CSV must be UTF-8
- First row = header
- Studio uses local path
- Java uses classpath

---

## Flow
```
CSV → Java → Jasper → PDF
```

---

## Summary
- Clean data before Jasper
- Flexible CSV support
- Same jrxml for Studio + runtime

---

## Future
- CSV preview
- Column mapping UI
- Excel support
- Multi-template