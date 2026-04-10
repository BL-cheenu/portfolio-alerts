# 📈 Portfolio Alerts App — US4: Stock Master Table

> Branch: `feature/US4-stock-master`

---

## 🎯 Goal
System stores NSE Top 50 stocks (company name + ticker symbol) as master data. Auto-loaded on startup and used for validation across all screens.

## 👤 Actor
System + Authenticated User

## 📋 Flow
1. NSE Top 50 stocks auto-loaded via `data.sql` on app startup
2. Frontend loads full list on app initialisation
3. User searches stocks by ticker or company name
4. File upload tickers cross-checked against master table

---

## 🔧 Concepts Used
| Concept | Implementation |
|---|---|
| DB Seeding | `data.sql` — 50 NSE stocks auto-inserted |
| JPA Repository | `StockRepository.java` — search queries |
| Stream API | Filter and map stock results |
| Logger | SLF4J in all classes |

---

## 📁 Files in This Branch

```
src/main/java/com/ch/
├── controller/
│   └── StockMasterController.java
├── dto/
│   ├── StockMasterDto.java
│   └── StockValidationDto.java
├── entity/
│   └── StockEntity.java
├── repository/
│   └── StockRepository.java
├── service/
│   └── StockMasterService.java
└── serviceImpl/
    └── StockMasterServiceImpl.java

src/main/resources/
└── data.sql                         ← NSE Top 50 seed data
```

---

## 🌐 API Endpoints

```
GET  /api/v1/stocks              → Load all stocks
GET  /api/v1/stocks/search?q=    → Search by ticker or company name
POST /api/v1/stocks/validate     → Validate tickers from file upload
Authorization: Bearer <token>
```

### GET /api/v1/stocks Response (200)
```json
{
  "msg": "Stocks fetched successfully.",
  "dataList": [
    { "id": 1, "companyName": "Reliance Industries Ltd", "tickerSymbol": "RELIANCE", "exchange": "NSE" },
    { "id": 2, "companyName": "Tata Consultancy Services", "tickerSymbol": "TCS", "exchange": "NSE" }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

### GET /api/v1/stocks/search?q=tata Response (200)
```json
{
  "dataList": [
    { "companyName": "Tata Consultancy Services", "tickerSymbol": "TCS", "exchange": "NSE" },
    { "companyName": "Tata Motors Ltd", "tickerSymbol": "TATAMOTORS", "exchange": "NSE" },
    { "companyName": "Tata Steel Ltd", "tickerSymbol": "TATASTEEL", "exchange": "NSE" }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

### POST /api/v1/stocks/validate Request
```json
["RELIANCE", "TCS", "FAKESTOCK", "WIPRO"]
```

### POST /api/v1/stocks/validate Response (200)
```json
{
  "msg": "1 invalid ticker(s) found.",
  "data": {
    "validTickers": ["RELIANCE", "TCS", "WIPRO"],
    "invalidTickers": ["FAKESTOCK"],
    "totalCount": 4,
    "validCount": 3,
    "invalidCount": 1
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

---

## ⚙️ application.properties
```properties
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
```

---

## 🌿 Git Commands

```bash
git checkout -b feature/US4-stock-master
git add .
git commit -m "feat(US4): implement stock master table with NSE 50 data

- stocks table with company name + ticker symbol
- GET /api/v1/stocks - load all stocks on app init
- GET /api/v1/stocks/search?q= - search by ticker/company
- POST /api/v1/stocks/validate - cross-check file upload tickers
- data.sql with NSE Top 50 seed data

Closes #US4"
git push origin feature/US4-stock-master
```
