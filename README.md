# 📈 Portfolio Alerts App — Backend

> A Spring Boot REST API for an investment portfolio management system with real-time stock monitoring and alert capabilities.

---

## 👤 Profile

| Field | Details |
|---|---|
| **Project Name** | Portfolio Alerts App |
| **Artifact ID** | `portfolio-alerts` |
| **Group ID** | `com.ch` |
| **Version** | `0.0.1-SNAPSHOT` |
| **Language** | Java 21 |
| **Framework** | Spring Boot 4.0.5 |
| **Database** | MySQL |
| **Authentication** | JWT (HS256) |
| **Build Tool** | Maven |

---

## 📋 Description

Portfolio Alerts App is a backend REST API that allows authenticated investors to:
- Register and login securely
- View a home dashboard with NSE Top 50 live stock ticker
- Upload portfolio via Excel or add stocks one by one via UI form
- Validate stocks against NSE master list
- View live portfolio valuation with profit/loss
- Manage portfolio — update quantity/price, delete one or all stocks
- Set upper and lower % threshold alerts for stocks
- Monitor portfolio performance in real time

---

## 🏗️ Project Structure

```
portfolio-alerts/
├── src/
│   ├── main/
│   │   ├── java/com/ch/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── UserRegistrationController.java
│   │   │   │   ├── UserLoginController.java
│   │   │   │   ├── HomeController.java
│   │   │   │   ├── StockMasterController.java
│   │   │   │   ├── PortfolioController.java
│   │   │   │   ├── PortfolioUploadController.java
│   │   │   │   ├── ManagePortfolioController.java
│   │   │   │   └── AlertController.java              ← US8
│   │   │   ├── customexception/
│   │   │   │   ├── UserRegistrationException.java
│   │   │   │   └── UserLoginException.java
│   │   │   ├── dto/
│   │   │   │   ├── CommonDto.java
│   │   │   │   ├── UserRegisterDto.java
│   │   │   │   ├── LoginRequestDto.java
│   │   │   │   ├── LoginResponseDto.java
│   │   │   │   ├── HomeResponseDto.java
│   │   │   │   ├── StockDto.java
│   │   │   │   ├── StockMasterDto.java
│   │   │   │   ├── StockValidationDto.java
│   │   │   │   ├── CreatePortfolioRequestDto.java
│   │   │   │   ├── PortfolioItemDto.java
│   │   │   │   ├── PortfolioValuationDto.java
│   │   │   │   ├── UpdatePortfolioRequestDto.java
│   │   │   │   ├── AlertRequestDto.java              ← US8
│   │   │   │   ├── AlertResponseDto.java             ← US8
│   │   │   │   ├── UploadRowDto.java
│   │   │   │   ├── UploadPreviewDto.java
│   │   │   │   ├── UploadConfirmDto.java
│   │   │   │   └── UploadResultDto.java
│   │   │   ├── entity/
│   │   │   │   ├── UserEntity.java
│   │   │   │   ├── StockEntity.java
│   │   │   │   ├── PortfolioEntity.java
│   │   │   │   └── AlertEntity.java                  ← US8
│   │   │   ├── filter/
│   │   │   │   └── JwtAuthFilter.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── StockRepository.java
│   │   │   │   ├── PortfolioRepository.java
│   │   │   │   └── AlertRepository.java              ← US8
│   │   │   ├── service/
│   │   │   │   ├── UserRegistrationService.java
│   │   │   │   ├── UserLoginService.java
│   │   │   │   ├── HomeService.java
│   │   │   │   ├── StockService.java
│   │   │   │   ├── StockMasterService.java
│   │   │   │   ├── PortfolioService.java
│   │   │   │   ├── PortfolioUploadService.java
│   │   │   │   ├── ManagePortfolioService.java
│   │   │   │   └── AlertService.java                 ← US8
│   │   │   ├── serviceImpl/
│   │   │   │   ├── UserRegistrationServiceImpl.java
│   │   │   │   ├── UserLoginServiceImpl.java
│   │   │   │   ├── HomeServiceImpl.java
│   │   │   │   ├── StockServiceImpl.java
│   │   │   │   ├── StockMasterServiceImpl.java
│   │   │   │   ├── PortfolioServiceImpl.java
│   │   │   │   ├── PortfolioUploadServiceImpl.java
│   │   │   │   ├── ManagePortfolioServiceImpl.java
│   │   │   │   └── AlertServiceImpl.java             ← US8
│   │   │   └── utils/
│   │   │       ├── UserInputValidator.java
│   │   │       ├── JwtUtil.java
│   │   │       ├── ExcelParserUtil.java
│   │   │       └── NseTop50Symbols.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql
│   └── test/
│       └── java/com/ch/
│           ├── utils/UserInputValidatorTest.java
│           ├── serviceImpl/
│           │   ├── UserRegistrationServiceImplTest.java
│           │   ├── PortfolioServiceImplTest.java
│           │   ├── PortfolioUploadServiceImplTest.java
│           │   ├── ManagePortfolioServiceImplTest.java
│           │   └── AlertServiceImplTest.java          ← US8
│           └── controller/
│               └── UserRegistrationControllerTest.java
├── pom.xml
└── README.md
```

---

## 🗄️ Database Tables

| Table | Description |
|---|---|
| `users` | Registered user credentials |
| `stocks` | NSE Top 50 stock master (seeded via data.sql) |
| `portfolio` | User's stock holdings with buy price |
| `alerts` | User's stock alert thresholds (upper/lower %) |

---

## 🔐 Authentication Flow

```
Register → Login → Get JWT Token → Use Token in Authorization Header
```
All endpoints except `/api/v1/auth/**` require:
```
Authorization: Bearer <JWT_TOKEN>
```

---

## 🚦 US Progress Tracker

| US | User Story | Status | Branch |
|---|---|---|---|
| **US1** | User Registration | ✅ Completed | `feature/US1-user-registration` |
| **US2** | User Login & Authentication | ✅ Completed | `feature/US2-user-login` |
| **US3** | Home Page with NSE Ticker | ✅ Completed | `feature/US3-home-page` |
| **US4** | Stock Master Table | ✅ Completed | `feature/US4-stock-master` |
| **US5** | Upload Portfolio (Excel) | ✅ Completed | `feature/US5-portfolio-upload` |
| **US6** | Create Portfolio (UI Form) | ✅ Completed | `feature/US6-create-portfolio` |
| **US7** | Manage Portfolio (Update/Delete) | ✅ Completed | `feature/US7-manage-portfolio` |
| **US8** | Alert Threshold Setting | ✅ Completed | `feature/US8-alert-threshold` |
| **US9** | Monitor Portfolio | 🔲 Pending | - |
| **US10** | Reports / Dashboard | 🔲 Pending | - |

---

## 📦 Tech Stack & Dependencies

| Dependency | Version | Purpose |
|---|---|---|
| Spring Boot | 4.0.5 | Framework |
| Spring Data JPA | - | ORM / DB |
| Spring Security Crypto | - | BCrypt encryption |
| MySQL Connector | - | Database driver |
| JJWT | 0.11.5 | JWT token |
| Apache POI | 5.2.5 | Excel parsing |
| Lombok | - | Boilerplate reduction |
| JUnit 5 | 5.10.1 | Unit testing |
| Mockito | 5.8.0 | Mocking |
| JaCoCo | 0.8.11 | Code coverage (min 80%) |

---

## US1 — User Registration

**Goal:** Allow a new user to register securely with encrypted credentials stored in MySQL.

**Actor:** End User (Investor)

**Flow:**
1. User submits Name, Username, Email, Password
2. Validated via Java 8 Predicate
3. Password encrypted with BCrypt (strength 12)
4. Persisted via JPA

**API:** `POST /api/v1/auth/register`

**Request:**
```json
{ "name": "John Doe", "username": "johndoe", "email": "john@example.com", "password": "Secret@123" }
```
**Response (201):**
```json
{ "msg": "User registered successfully.", "data": { "name": "John Doe", "username": "johndoe", "email": "john@example.com" }, "status": "SUCCESS", "statusCode": 201 }
```
**Failure (400):**
```json
{ "msg": "Invalid email format.", "status": "FAILED", "statusCode": 400 }
```

---

## US2 — User Login & Authentication

**Goal:** Allow a registered user to login and receive a JWT token.

**Actor:** Registered User

**API:** `POST /api/v1/auth/login`

**Request:**
```json
{ "username": "johndoe", "password": "Secret@123" }
```
**Response (200):**
```json
{ "msg": "Login successful.", "data": { "username": "johndoe", "email": "john@example.com", "token": "eyJhbGciOiJIUzI1NiJ9.xxxxx" }, "status": "SUCCESS", "statusCode": 200 }
```
**Failure (401):**
```json
{ "msg": "Invalid username or password.", "status": "FAILED", "statusCode": 401 }
```

---

## US3 — Home Page with NSE Ticker

**Goal:** Authenticated user views menu options and NSE Top 50 ticker.

**API:** `GET /api/v1/home`

**Response (200):**
```json
{
  "data": {
    "welcomeMessage": "Welcome to Portfolio Alerts App!",
    "username": "johndoe",
    "menuOptions": ["Portfolio Creation / Updation", "Alert Setting", "Monitor Portfolio"],
    "nseTop50Ticker": [{ "symbol": "RELIANCE", "currentPrice": 2845.50, "change": 12.30, "changePercent": 0.43 }]
  },
  "status": "SUCCESS", "statusCode": 200
}
```

---

## US4 — Stock Master Table

**Goal:** NSE Top 50 stocks stored as master data for validation.

**APIs:**
```
GET  /api/v1/stocks
GET  /api/v1/stocks/search?q=tata
POST /api/v1/stocks/validate
```

**Validate Request/Response:**
```json
// Request
["RELIANCE", "TCS", "FAKESTOCK"]
// Response
{ "data": { "validTickers": ["RELIANCE","TCS"], "invalidTickers": ["FAKESTOCK"], "totalCount": 3, "validCount": 2, "invalidCount": 1 }, "status": "SUCCESS" }
```

---

## US5 — Upload Portfolio (Excel)

**Goal:** Upload Excel file with stock holdings — new added, existing updated with consent.

**APIs:**
```
POST /api/v1/portfolio/upload/preview
POST /api/v1/portfolio/upload/confirm
```

**Excel Format:** `Stock Symbol | Company Name | Quantity | Buy Price`

**Preview Response:**
```json
{ "data": { "newStocks": [{ "stockSymbol": "WIPRO", "quantity": 20, "buyPrice": 450.0 }], "conflictStocks": [{ "stockSymbol": "TCS", "quantity": 5, "buyPrice": 3900.0 }], "invalidStocks": ["FAKESTOCK"], "newCount": 1, "conflictCount": 1, "invalidCount": 1 }, "status": "SUCCESS" }
```

**Confirm Request:**
```json
{ "newStocks": [{ "stockSymbol": "WIPRO", "quantity": 20, "buyPrice": 450.0 }], "updateStocks": [{ "stockSymbol": "TCS", "quantity": 5, "buyPrice": 3900.0 }] }
```

---

## US6 — Create Portfolio (UI Form)

**Goal:** Add stocks one by one via form with live valuation using Stream API.

**APIs:**
```
POST /api/v1/portfolio
GET  /api/v1/portfolio/valuation
```

**Add Request:**
```json
{ "stockSymbol": "RELIANCE", "companyName": "Reliance Industries", "quantity": 10, "buyPrice": 2800.00 }
```

**Valuation Response:**
```json
{
  "data": {
    "holdings": [{ "stockSymbol": "RELIANCE", "quantity": 10, "buyPrice": 2800.0, "currentPrice": 3000.0, "totalInvested": 28000.0, "currentValue": 30000.0, "profitLoss": 2000.0, "profitLossPercent": 7.14 }],
    "totalInvested": 28000.0, "totalCurrentValue": 30000.0, "totalProfitLoss": 2000.0, "totalProfitLossPercent": 7.14, "totalStocks": 1
  },
  "status": "SUCCESS"
}
```

---

## US7 — Manage Portfolio (Update / Delete)

**Goal:** View, update, and delete portfolio stocks with `@Transactional` rollback.

**APIs:**
```
GET    /api/v1/portfolio
PUT    /api/v1/portfolio/{id}
DELETE /api/v1/portfolio/{id}
DELETE /api/v1/portfolio
```

**Update Request:**
```json
{ "quantity": 20, "buyPrice": 2500.00 }
```

**Update Response (200):**
```json
{ "msg": "Stock 'RELIANCE' updated successfully.", "data": { "stockSymbol": "RELIANCE", "quantity": 20, "buyPrice": 2500.0, "totalInvested": 50000.0, "currentValue": 60000.0, "profitLoss": 10000.0 }, "status": "SUCCESS" }
```

**Delete One (200):**
```json
{ "msg": "Stock 'RELIANCE' deleted successfully.", "status": "SUCCESS", "statusCode": 200 }
```

**Delete All (200):**
```json
{ "msg": "All 3 stocks deleted successfully.", "status": "SUCCESS", "statusCode": 200 }
```

---

## US8 — Alert Threshold Setting

**Goal:** Authenticated user sets upper and lower % thresholds for stock price alerts.

**Actor:** Authenticated User

**Flow:**
1. User selects a stock and sets upper % and lower % threshold
2. Backend validates stock and saves thresholds
3. Alert prices calculated: `buyPrice ± (buyPrice × threshold%)`
4. User can update or delete thresholds

**Alert Price Formula:**
```
upperAlertPrice = buyPrice + (buyPrice × upperThreshold / 100)
lowerAlertPrice = buyPrice - (buyPrice × lowerThreshold / 100)

Example: buyPrice=2800, upper=10%, lower=5%
  upperAlertPrice = 2800 + 280 = 3080
  lowerAlertPrice = 2800 - 140 = 2660
```

**APIs:**
```
POST   /api/v1/alerts              → Set new alert
PUT    /api/v1/alerts/{id}         → Update threshold
GET    /api/v1/alerts              → Get all alerts
GET    /api/v1/alerts/stock/{sym}  → Get alert by stock
DELETE /api/v1/alerts/{id}         → Delete alert
Authorization: Bearer <token>
```

**POST /api/v1/alerts Request:**
```json
{
  "stockSymbol": "RELIANCE",
  "companyName": "Reliance Industries",
  "upperThreshold": 10.0,
  "lowerThreshold": 5.0
}
```

**POST /api/v1/alerts Response (201):**
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

**PUT /api/v1/alerts/1 Request:**
```json
{ "upperThreshold": 15.0 }
```

**PUT /api/v1/alerts/1 Response (200):**
```json
{
  "msg": "Alert for 'RELIANCE' updated successfully.",
  "data": {
    "stockSymbol": "RELIANCE",
    "upperThreshold": 15.0,
    "lowerThreshold": 5.0,
    "upperAlertPrice": 3220.0,
    "lowerAlertPrice": 2660.0
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

**GET /api/v1/alerts Response (200):**
```json
{
  "msg": "Alerts fetched successfully.",
  "dataList": [
    { "id": 1, "stockSymbol": "RELIANCE", "upperThreshold": 10.0, "lowerThreshold": 5.0, "buyPrice": 2800.0, "upperAlertPrice": 3080.0, "lowerAlertPrice": 2660.0, "isActive": true },
    { "id": 2, "stockSymbol": "TCS", "upperThreshold": 8.0, "lowerThreshold": 4.0, "buyPrice": 3900.0, "upperAlertPrice": 4212.0, "lowerAlertPrice": 3744.0, "isActive": true }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

**DELETE /api/v1/alerts/1 Response (200):**
```json
{ "msg": "Alert for 'RELIANCE' deleted successfully.", "status": "SUCCESS", "statusCode": 200 }
```

**Failure Response (400):**
```json
{ "msg": "Upper threshold must be greater than 0%.", "status": "FAILED", "statusCode": 400 }
```

---

## ▶️ How to Run

```bash
# 1. Create MySQL database
mysql -u root -p
CREATE DATABASE investor_db;

# 2. Update application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/investor_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true

# 3. Run
mvn spring-boot:run

# 4. Test
mvn test

# 5. Coverage report
mvn verify
# target/site/jacoco/index.html
```

---

## 🌿 Git Branch Strategy

```
main
├── feature/US1-user-registration
├── feature/US2-user-login
├── feature/US3-home-page
├── feature/US4-stock-master
├── feature/US5-portfolio-upload
├── feature/US6-create-portfolio
├── feature/US7-manage-portfolio
└── feature/US8-alert-threshold
```

---

## 📬 Postman Collection Order

```
1.  POST   /api/v1/auth/register                 → Register
2.  POST   /api/v1/auth/login                    → Login + token
3.  GET    /api/v1/home                          → Home page
4.  GET    /api/v1/stocks                        → Stock master
5.  GET    /api/v1/stocks/search?q=tata          → Search
6.  POST   /api/v1/stocks/validate               → Validate tickers
7.  POST   /api/v1/portfolio/upload/preview      → Excel preview
8.  POST   /api/v1/portfolio/upload/confirm      → Excel confirm
9.  POST   /api/v1/portfolio                     → Add stock
10. GET    /api/v1/portfolio/valuation           → Valuation
11. GET    /api/v1/portfolio                     → View portfolio
12. PUT    /api/v1/portfolio/{id}                → Update stock
13. DELETE /api/v1/portfolio/{id}                → Delete one
14. DELETE /api/v1/portfolio                     → Delete all
15. POST   /api/v1/alerts                        → Set alert
16. PUT    /api/v1/alerts/{id}                   → Update alert
17. GET    /api/v1/alerts                        → Get all alerts
18. GET    /api/v1/alerts/stock/RELIANCE         → Get by stock
19. DELETE /api/v1/alerts/{id}                   → Delete alert
```
