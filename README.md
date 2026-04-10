# 📈 Portfolio Alerts App — US8: Alert Threshold Setting

> Branch: `feature/US8-alert-threshold`

---

## 🎯 Goal
Authenticated user sets upper and lower percentage thresholds for stock price alerts. Alert prices calculated from buy price and threshold %.

## 👤 Actor
Authenticated User

## 📋 Flow
1. User selects a stock and sets upper % and lower % threshold
2. Backend validates stock against master table
3. Thresholds saved to `alerts` DB table
4. Alert prices calculated and returned
5. User can update or delete thresholds anytime

---

## 🔧 Concepts Used
| Concept | Implementation |
|---|---|
| Alert price formula | `buyPrice ± (buyPrice × threshold% / 100)` |
| Stock master validation | `StockRepository.existsByTickerSymbolIgnoreCase()` |
| Unique constraint | One alert per user per stock |
| Logger | SLF4J in all classes |

---

## 📁 Files in This Branch

```
src/main/java/com/ch/
├── controller/
│   └── AlertController.java
├── dto/
│   ├── AlertRequestDto.java
│   └── AlertResponseDto.java
├── entity/
│   └── AlertEntity.java             ← alerts table
├── repository/
│   └── AlertRepository.java
├── service/
│   └── AlertService.java
└── serviceImpl/
    └── AlertServiceImpl.java

src/test/java/com/ch/serviceImpl/
└── AlertServiceImplTest.java
```

---

## 🧮 Alert Price Formula

```
upperAlertPrice = buyPrice + (buyPrice × upperThreshold / 100)
lowerAlertPrice = buyPrice - (buyPrice × lowerThreshold / 100)

Example: buyPrice = ₹2800, upper = 10%, lower = 5%
  upperAlertPrice = 2800 + 280 = ₹3080  ← alert when price ≥ this
  lowerAlertPrice = 2800 - 140 = ₹2660  ← alert when price ≤ this
```

---

## 🌐 API Endpoints

```
POST   /api/v1/alerts                  → Set new alert
PUT    /api/v1/alerts/{id}             → Update threshold
GET    /api/v1/alerts                  → Get all alerts
GET    /api/v1/alerts/stock/{symbol}   → Get alert by stock
DELETE /api/v1/alerts/{id}             → Delete alert
Authorization: Bearer <token>
```

### POST /api/v1/alerts Request
```json
{
  "stockSymbol": "RELIANCE",
  "companyName": "Reliance Industries",
  "upperThreshold": 10.0,
  "lowerThreshold": 5.0
}
```

### POST /api/v1/alerts Response (201)
```json
{
  "msg": "Alert set for 'RELIANCE' successfully.",
  "data": {
    "id": 1,
    "stockSymbol": "RELIANCE",
    "companyName": "Reliance Industries",
    "upperThreshold": 10.0,
    "lowerThreshold": 5.0,
    "buyPrice": 2800.0,
    "upperAlertPrice": 3080.0,
    "lowerAlertPrice": 2660.0,
    "isActive": true
  },
  "status": "SUCCESS",
  "statusCode": 201
}
```

### PUT /api/v1/alerts/1 Request
```json
{ "upperThreshold": 15.0 }
```

### PUT /api/v1/alerts/1 Response (200)
```json
{
  "msg": "Alert for 'RELIANCE' updated successfully.",
  "data": {
    "upperThreshold": 15.0,
    "lowerThreshold": 5.0,
    "upperAlertPrice": 3220.0,
    "lowerAlertPrice": 2660.0
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

### GET /api/v1/alerts Response (200)
```json
{
  "msg": "Alerts fetched successfully.",
  "dataList": [
    { "stockSymbol": "RELIANCE", "upperThreshold": 10.0, "lowerThreshold": 5.0, "upperAlertPrice": 3080.0, "lowerAlertPrice": 2660.0, "isActive": true },
    { "stockSymbol": "TCS", "upperThreshold": 8.0, "lowerThreshold": 4.0, "upperAlertPrice": 4212.0, "lowerAlertPrice": 3744.0, "isActive": true }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

### ❌ Duplicate Alert (400)
```json
{
  "msg": "Alert already exists for 'RELIANCE'. Use update instead.",
  "status": "FAILED",
  "statusCode": 400
}

## 🌿 Git Commands

```bash
git checkout -b feature/US8-alert-threshold
git add .
git commit -m "feat(US8): implement alert threshold setting

- POST /api/v1/alerts        - set upper/lower % threshold
- PUT  /api/v1/alerts/{id}   - update threshold
- GET  /api/v1/alerts        - get all alerts
- GET  /api/v1/alerts/stock/{sym} - get by stock
- DELETE /api/v1/alerts/{id} - delete alert
- Alert price calculation from buy price + threshold%
- 9 unit tests

Closes #US8"
git push origin feature/US8-alert-threshold
```
