# 📈 Portfolio Alerts App — US6: Create Portfolio (UI Form)

> Branch: `feature/US6-create-portfolio`

---

## 🎯 Goal
Authenticated user adds individual stocks one by one via UI form. Portfolio valuation computed using Stream API with buy price comparison.

## 👤 Actor
Authenticated User

## 📋 Flow
1. User selects a valid stock from master list
2. Enters quantity and buy price
3. Backend validates stock symbol against master table
4. Stock saved to `portfolio` table
5. Portfolio valuation returned — profit/loss computed via Stream API

---

## 🔧 Concepts Used
| Concept | Implementation |
|---|---|
| Stream API | `mapToDouble().sum()` — total invested, current value, P&L |
| Stock master validation | `StockRepository.existsByTickerSymbolIgnoreCase()` |
| JPA persistence | `PortfolioRepository.save()` |
| Logger | SLF4J in all classes |

---

## 📁 Files in This Branch

```
src/main/java/com/ch/
├── controller/
│   └── PortfolioController.java
├── dto/
│   ├── CreatePortfolioRequestDto.java
│   ├── PortfolioItemDto.java          ← single stock with P&L
│   └── PortfolioValuationDto.java     ← overall summary
├── service/
│   └── PortfolioService.java
└── serviceImpl/
    └── PortfolioServiceImpl.java      ← Stream API valuation

src/test/java/com/ch/serviceImpl/
└── PortfolioServiceImplTest.java
```

---

## 🌐 API Endpoints

```
POST /api/v1/portfolio              → Add one stock
GET  /api/v1/portfolio/valuation    → Portfolio with valuation
Authorization: Bearer <token>
```

### POST /api/v1/portfolio Request
```json
{
  "stockSymbol": "RELIANCE",
  "companyName": "Reliance Industries",
  "quantity": 10,
  "buyPrice": 2800.00
}
```

### POST /api/v1/portfolio Response (201)
```json
{
  "msg": "Stock 'RELIANCE' added to portfolio successfully.",
  "data": {
    "id": 1,
    "stockSymbol": "RELIANCE",
    "companyName": "Reliance Industries",
    "quantity": 10,
    "buyPrice": 2800.0,
    "currentPrice": 2800.0,
    "totalInvested": 28000.0,
    "currentValue": 28000.0,
    "profitLoss": 0.0,
    "profitLossPercent": 0.0
  },
  "status": "SUCCESS",
  "statusCode": 201
}
```

### GET /api/v1/portfolio/valuation Response (200)
```json
{
  "msg": "Portfolio valuation fetched successfully.",
  "data": {
    "holdings": [
      {
        "stockSymbol": "RELIANCE", "quantity": 10,
        "buyPrice": 2800.0, "currentPrice": 3000.0,
        "totalInvested": 28000.0, "currentValue": 30000.0,
        "profitLoss": 2000.0, "profitLossPercent": 7.14
      },
      {
        "stockSymbol": "TCS", "quantity": 5,
        "buyPrice": 3900.0, "currentPrice": 4000.0,
        "totalInvested": 19500.0, "currentValue": 20000.0,
        "profitLoss": 500.0, "profitLossPercent": 2.56
      }
    ],
    "totalInvested": 47500.0,
    "totalCurrentValue": 50000.0,
    "totalProfitLoss": 2500.0,
    "totalProfitLossPercent": 5.26,
    "totalStocks": 2
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

### ❌ Invalid Stock (400)
```json
{
  "msg": "Stock 'FAKESTOCK' is not valid. Please select from the stock master list.",
  "status": "FAILED",
  "statusCode": 400
}
```

### ❌ Duplicate Stock (400)
```json
{
  "msg": "Stock 'RELIANCE' already exists in your portfolio. Use update instead.",
  "status": "FAILED",
  "statusCode": 400
}
```

---

## 🧮 Stream API — Valuation Calculation
```java
// Total invested
double totalInvested = holdings.stream()
    .mapToDouble(PortfolioItemDto::getTotalInvested).sum();

// Total current value
double totalCurrentValue = holdings.stream()
    .mapToDouble(PortfolioItemDto::getCurrentValue).sum();

// Total P&L
double totalProfitLoss = holdings.stream()
    .mapToDouble(PortfolioItemDto::getProfitLoss).sum();
```

---

## 🧪 Unit Tests
| Test | Coverage |
|---|---|
| `testAddStock_Success` | Valid stock added |
| `testAddStock_BlankSymbol` | Validation |
| `testAddStock_ZeroQuantity` | Validation |
| `testAddStock_InvalidSymbol` | Master check |
| `testAddStock_DuplicateStock` | Duplicate check |
| `testGetValuation_CorrectTotals` | Stream API math |
| `testGetValuation_EmptyPortfolio` | Edge case |
| `testGetValuation_UserNotFound` | 404 |

---

## 🌿 Git Commands

```bash
git checkout -b feature/US6-create-portfolio
git add .
git commit -m "feat(US6): implement portfolio creation with Stream API valuation

- POST /api/v1/portfolio - add stock via UI form
- GET /api/v1/portfolio/valuation - valuation with P&L
- Stream API for totalInvested, currentValue, profitLoss
- Stock master validation before save
- Duplicate stock detection
- 8 unit tests with Mockito

Closes #US6"
git push origin feature/US6-create-portfolio
```
