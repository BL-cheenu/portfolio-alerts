# 📈 Portfolio Alerts App — US5: Upload Portfolio (Excel)

> Branch: `feature/US5-portfolio-upload`

---

## 🎯 Goal
Authenticated user uploads an Excel file with stock holdings. New stocks added, existing updated with user consent.

## 👤 Actor
Authenticated User

## 📋 Flow
1. User uploads `.xls` or `.xlsx` file
2. Apache POI parses each row
3. Each ticker validated against stock master table (US4)
4. Preview shown: new / conflict / invalid stocks
5. User reviews and consents to update conflicting stocks
6. Data saved to `portfolio` table

---

## 🔧 Concepts Used
| Concept | Implementation |
|---|---|
| Apache POI | `ExcelParserUtil.java` — reads `.xls` / `.xlsx` |
| Stock validation | Cross-check against `stocks` master table |
| 2-Step API | Preview → Confirm flow |
| Logger | SLF4J in all classes |

---

## 📁 Files in This Branch

```
src/main/java/com/ch/
├── controller/
│   └── PortfolioUploadController.java
├── dto/
│   ├── UploadRowDto.java
│   ├── UploadPreviewDto.java
│   ├── UploadConfirmDto.java
│   └── UploadResultDto.java
├── entity/
│   └── PortfolioEntity.java
├── repository/
│   └── PortfolioRepository.java
├── service/
│   └── PortfolioUploadService.java
├── serviceImpl/
│   └── PortfolioUploadServiceImpl.java
└── utils/
    └── ExcelParserUtil.java          ← Apache POI parser

src/test/java/com/ch/serviceImpl/
└── PortfolioUploadServiceImplTest.java
```

---

## 📊 Excel File Format
| Column A | Column B | Column C | Column D |
|---|---|---|---|
| Stock Symbol | Company Name | Quantity | Buy Price |
| RELIANCE | Reliance Industries | 10 | 2800.00 |
| TCS | Tata Consultancy | 5 | 3900.00 |

---

## 🌐 API Endpoints — 2 Step Flow

```
POST /api/v1/portfolio/upload/preview   → Step 1
POST /api/v1/portfolio/upload/confirm   → Step 2
Authorization: Bearer <token>
```

### Step 1 — Preview Request
```
Body: form-data
Key: file | Type: File | Value: portfolio.xls
```

### Step 1 — Preview Response (200)
```json
{
  "msg": "Preview generated. Please confirm to proceed.",
  "data": {
    "newStocks": [
      { "stockSymbol": "WIPRO", "companyName": "Wipro Ltd", "quantity": 20, "buyPrice": 450.0 }
    ],
    "conflictStocks": [
      { "stockSymbol": "TCS", "companyName": "TCS", "quantity": 5, "buyPrice": 3900.0 }
    ],
    "invalidStocks": ["FAKESTOCK"],
    "totalRows": 3,
    "newCount": 1,
    "conflictCount": 1,
    "invalidCount": 1
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

### Step 2 — Confirm Request
```json
{
  "newStocks": [
    { "stockSymbol": "WIPRO", "companyName": "Wipro Ltd", "quantity": 20, "buyPrice": 450.0 }
  ],
  "updateStocks": [
    { "stockSymbol": "TCS", "companyName": "TCS", "quantity": 5, "buyPrice": 3900.0 }
  ]
}
```

### Step 2 — Confirm Response (200)
```json
{
  "msg": "Upload complete. Added: 1, Updated: 1, Skipped: 0",
  "data": {
    "addedCount": 1,
    "updatedCount": 1,
    "skippedCount": 0,
    "message": "Upload complete. Added: 1, Updated: 1, Skipped: 0"
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

---

## 📦 pom.xml Dependencies
```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.5</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

---

## 🧪 Unit Tests
| Test Class | Tests |
|---|---|
| `PortfolioUploadServiceImplTest` | 7 tests — empty file, new/conflict/invalid classify, add/update/user-not-found |

---

## 🌿 Git Commands

```bash
git checkout -b feature/US5-portfolio-upload
git add .
git commit -m "feat(US5): implement portfolio upload with Excel parsing

- POST /api/v1/portfolio/upload/preview - parse + classify
- POST /api/v1/portfolio/upload/confirm - save with user consent
- Apache POI for .xls/.xlsx parsing
- Stock master validation for each ticker
- Conflict detection for existing portfolio entries
- 7 unit tests with Mockito

Closes #US5"
git push origin feature/US5-portfolio-upload
```
