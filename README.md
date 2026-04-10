# 📈 Portfolio Alerts App — US3: Home Page with NSE Ticker

> Branch: `feature/US3-home-page`

---

## 🎯 Goal
Authenticated user views menu options and NSE Top 50 real-time stock ticker after login.

## 👤 Actor
Authenticated User

## 📋 Flow
1. User lands on home page after successful login
2. Backend returns menu options
3. NSE Top 50 prices fetched via Alpha Vantage API
4. Ticker displayed at bottom of UI

---

## 🔧 Concepts Used
| Concept | Implementation |
|---|---|
| JWT Filter | `JwtAuthFilter.java` — validates Bearer token |
| Alpha Vantage API | `StockServiceImpl.java` — fetches real-time NSE prices |
| RestTemplate | HTTP calls to external API |
| Logger | SLF4J in all classes |

---

## 📁 Files in This Branch

```
src/main/java/com/ch/
├── config/
│   └── SecurityConfig.java          ← JWT Filter registration
├── controller/
│   └── HomeController.java          ← GET /api/v1/home
├── dto/
│   ├── HomeResponseDto.java
│   └── StockDto.java
├── service/
│   ├── HomeService.java
│   └── StockService.java
├── serviceImpl/
│   ├── HomeServiceImpl.java
│   └── StockServiceImpl.java        ← Alpha Vantage API
└── utils/
    └── NseTop50Symbols.java         ← NSE Top 50 symbol list
    └── JwtAuthFilter.java           ← JWT token validation filter
```

---

## 🌐 API Endpoint

```
GET /api/v1/home
Authorization: Bearer <token>
```

### ✅ Success Response (200)
```json
{
  "msg": "Home page loaded successfully.",
  "data": {
    "welcomeMessage": "Welcome to Portfolio Alerts App!",
    "username": "johndoe",
    "menuOptions": [
      "Portfolio Creation / Updation",
      "Alert Setting",
      "Monitor Portfolio"
    ],
    "nseTop50Ticker": [
      {
        "symbol": "RELIANCE",
        "companyName": "Reliance Industries Ltd",
        "currentPrice": 2845.50,
        "change": 12.30,
        "changePercent": 0.43
      }
    ]
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

### ❌ Missing Token (401)
```json
{
  "msg": "Authorization token missing.",
  "status": "FAILED",
  "statusCode": 401
}
```

---

## ⚙️ application.properties
```properties
stock.api.key=YOUR_ALPHA_VANTAGE_KEY
```
Get free API key: https://www.alphavantage.co/support/#api-key

---

## 🌿 Git Commands

```bash
git checkout -b feature/US3-home-page
git add .
git commit -m "feat(US3): implement home page with menu and NSE Top 50 ticker

- GET /api/v1/home protected endpoint
- JwtAuthFilter for token validation
- Alpha Vantage API for real-time NSE Top 50 prices
- Menu options: Portfolio, Alert Setting, Monitor Portfolio
- SecurityConfig updated with JWT filter registration

Closes #US3"
git push origin feature/US3-home-page
```
