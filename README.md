# 📈 Portfolio Alerts App — US7: Manage Portfolio (Update / Delete)

> Branch: `feature/US7-manage-portfolio`

---

## 🎯 Goal
Authenticated user can view, update (quantity/buy price), and delete one or all stocks from portfolio. All changes persisted to database.

## 👤 Actor
Authenticated User

## 📋 Flow
1. User views full portfolio with valuation
2. Selects a stock to update quantity or buy price
3. Or deletes a specific stock
4. Or clears entire portfolio
5. All changes saved via `@Transactional` with automatic rollback on failure

---

## 🔧 Concepts Used
| Concept | Implementation |
|---|---|
| `@Transactional` | Auto commit/rollback on update and delete |
| `rollbackFor = Exception.class` | Rollback on any exception |
| `@Transactional(readOnly = true)` | Performance for read-only operations |
| PUT HTTP method | Update endpoint |
| Stream API | Valuation in getPortfolio() |
| Logger | SLF4J in all classes |

---

## 📁 Files in This Branch

```
src/main/java/com/ch/
├── controller/
│   └── ManagePortfolioController.java
├── dto/
│   └── UpdatePortfolioRequestDto.java
├── repository/
│   └── PortfolioRepository.java      ← findByIdAndUser added
├── service/
│   └── ManagePortfolioService.java
└── serviceImpl/
    └── ManagePortfolioServiceImpl.java ← @Transactional

src/test/java/com/ch/serviceImpl/
└── ManagePortfolioServiceImplTest.java
```

---

## 🌐 API Endpoints

```
GET    /api/v1/portfolio        → View full portfolio
PUT    /api/v1/portfolio/{id}   → Update one stock
DELETE /api/v1/portfolio/{id}   → Delete one stock
DELETE /api/v1/portfolio        → Delete all stocks
Authorization: Bearer <token>
```

### GET /api/v1/portfolio Response (200)
```json
{
  "msg": "Portfolio fetched successfully.",
  "data": {
    "holdings": [
      {
        "id": 1, "stockSymbol": "RELIANCE", "quantity": 10,
        "buyPrice": 2800.0, "currentPrice": 3000.0,
        "totalInvested": 28000.0, "currentValue": 30000.0,
        "profitLoss": 2000.0, "profitLossPercent": 7.14
      }
    ],
    "totalInvested": 28000.0,
    "totalCurrentValue": 30000.0,
    "totalProfitLoss": 2000.0,
    "totalProfitLossPercent": 7.14,
    "totalStocks": 1
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

### PUT /api/v1/portfolio/1 Request
```json
{ "quantity": 20, "buyPrice": 2500.00 }
```

### PUT /api/v1/portfolio/1 Response (200)
```json
{
  "msg": "Stock 'RELIANCE' updated successfully.",
  "data": {
    "id": 1, "stockSymbol": "RELIANCE",
    "quantity": 20, "buyPrice": 2500.0,
    "totalInvested": 50000.0, "currentValue": 60000.0,
    "profitLoss": 10000.0, "profitLossPercent": 20.0
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

### DELETE /api/v1/portfolio/1 Response (200)
```json
{
  "msg": "Stock 'RELIANCE' deleted successfully.",
  "status": "SUCCESS",
  "statusCode": 200
}
```

### DELETE /api/v1/portfolio Response (200)
```json
{
  "msg": "All 3 stocks deleted successfully.",
  "status": "SUCCESS",
  "statusCode": 200
}
```

### ❌ Not Found (400)
```json
{
  "msg": "Portfolio record not found.",
  "status": "FAILED",
  "statusCode": 400
}
```

---

## 🧪 Unit Tests (10 tests)
| Test | What is Tested |
|---|---|
| `testGetPortfolio_Success` | Correct valuation |
| `testGetPortfolio_Empty` | Zero totals |
| `testGetPortfolio_UserNotFound` | FAILED |
| `testUpdateStock_QuantitySuccess` | Quantity update |
| `testUpdateStock_BuyPriceSuccess` | Price update |
| `testUpdateStock_NoFields` | Validation |
| `testUpdateStock_NotFound` | FAILED |
| `testDeleteStock_Success` | Delete one |
| `testDeleteStock_NotFound` | FAILED |
| `testDeleteAllStocks_Success` | Delete all |
| `testDeleteAllStocks_Empty` | Empty portfolio |

---

## 🌿 Git Commands

```bash
git checkout -b feature/US7-manage-portfolio
git add .
git commit -m "feat(US7): implement manage portfolio with Transactional rollback

- GET  /api/v1/portfolio         - view portfolio with valuation
- PUT  /api/v1/portfolio/{id}    - update quantity/buyPrice
- DELETE /api/v1/portfolio/{id}  - delete one stock
- DELETE /api/v1/portfolio       - delete all stocks
- @Transactional with rollbackFor Exception
- Stream API for valuation
- 10 unit tests

Closes #US7"
git push origin feature/US7-manage-portfolio
```
