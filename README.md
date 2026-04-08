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
- Update or delete portfolio stocks
- Set price alerts for stocks
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
│   │   │   │   └── ManagePortfolioController.java     ← US7
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
│   │   │   │   ├── UpdatePortfolioRequestDto.java     ← US7
│   │   │   │   ├── UploadRowDto.java
│   │   │   │   ├── UploadPreviewDto.java
│   │   │   │   ├── UploadConfirmDto.java
│   │   │   │   └── UploadResultDto.java
│   │   │   ├── entity/
│   │   │   │   ├── UserEntity.java
│   │   │   │   ├── StockEntity.java
│   │   │   │   └── PortfolioEntity.java
│   │   │   ├── filter/
│   │   │   │   └── JwtAuthFilter.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── StockRepository.java
│   │   │   │   └── PortfolioRepository.java
│   │   │   ├── service/
│   │   │   │   ├── UserRegistrationService.java
│   │   │   │   ├── UserLoginService.java
│   │   │   │   ├── HomeService.java
│   │   │   │   ├── StockService.java
│   │   │   │   ├── StockMasterService.java
│   │   │   │   ├── PortfolioService.java
│   │   │   │   ├── PortfolioUploadService.java
│   │   │   │   └── ManagePortfolioService.java        ← US7
│   │   │   ├── serviceImpl/
│   │   │   │   ├── UserRegistrationServiceImpl.java
│   │   │   │   ├── UserLoginServiceImpl.java
│   │   │   │   ├── HomeServiceImpl.java
│   │   │   │   ├── StockServiceImpl.java
│   │   │   │   ├── StockMasterServiceImpl.java
│   │   │   │   ├── PortfolioServiceImpl.java
│   │   │   │   ├── PortfolioUploadServiceImpl.java
│   │   │   │   └── ManagePortfolioServiceImpl.java    ← US7
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
│           ├── utils/
│           │   └── UserInputValidatorTest.java
│           ├── serviceImpl/
│           │   ├── UserRegistrationServiceImplTest.java
│           │   ├── PortfolioServiceImplTest.java
│           │   ├── PortfolioUploadServiceImplTest.java
│           │   └── ManagePortfolioServiceImplTest.java ← US7
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
| **US8** | Alert Setting | 🔲 Pending | - |
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
2. Backend validates email format and password rules via Java 8 Predicate
3. Password encrypted with BCrypt (strength 12)
4. User entity persisted via JPA
5. Success response returned

**Password Rules:** Min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char (`@#$%^*-_`)

**API Endpoint:**
```
POST /api/v1/auth/register
Content-Type: application/json
```

**Request:**
```json
{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "Secret@123"
}
```

**Response — Success (201):**
```json
{
  "msg": "User registered successfully.",
  "data": { "name": "John Doe", "username": "johndoe", "email": "john@example.com" },
  "status": "SUCCESS",
  "statusCode": 201
}
```

**Response — Failure (400):**
```json
{ "msg": "Invalid email format.", "status": "FAILED", "statusCode": 400 }
```

---

## US2 — User Login & Authentication

**Goal:** Allow a registered user to login and receive a JWT token.

**Actor:** Registered User

**Flow:**
1. User submits username and password
2. Backend fetches user via `Optional<UserEntity>`
3. BCrypt password verification
4. JWT token generated (24hr expiry, HS256)

**API Endpoint:**
```
POST /api/v1/auth/login
Content-Type: application/json
```

**Request:**
```json
{ "username": "johndoe", "password": "Secret@123" }
```

**Response — Success (200):**
```json
{
  "msg": "Login successful.",
  "data": { "username": "johndoe", "email": "john@example.com", "token": "eyJhbGciOiJIUzI1NiJ9.xxxxx" },
  "status": "SUCCESS",
  "statusCode": 200
}
```

**Response — Failure (401):**
```json
{ "msg": "Invalid username or password.", "status": "FAILED", "statusCode": 401 }
```

---

## US3 — Home Page with NSE Ticker

**Goal:** Authenticated user views menu options and NSE Top 50 stock ticker.

**Actor:** Authenticated User

**API Endpoint:**
```
GET /api/v1/home
Authorization: Bearer <token>
```

**Response — Success (200):**
```json
{
  "msg": "Home page loaded successfully.",
  "data": {
    "welcomeMessage": "Welcome to Portfolio Alerts App!",
    "username": "johndoe",
    "menuOptions": ["Portfolio Creation / Updation", "Alert Setting", "Monitor Portfolio"],
    "nseTop50Ticker": [
      { "symbol": "RELIANCE", "companyName": "Reliance Industries Ltd", "currentPrice": 2845.50, "change": 12.30, "changePercent": 0.43 }
    ]
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

---

## US4 — Stock Master Table

**Goal:** System stores NSE Top 50 stocks as master data for validation across all screens.

**API Endpoints:**
```
GET  /api/v1/stocks              → Load all stocks
GET  /api/v1/stocks/search?q=    → Search by ticker or company name
POST /api/v1/stocks/validate     → Validate tickers
```

**GET /api/v1/stocks Response:**
```json
{
  "msg": "Stocks fetched successfully.",
  "dataList": [
    { "id": 1, "companyName": "Reliance Industries Ltd", "tickerSymbol": "RELIANCE", "exchange": "NSE" }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

**POST /api/v1/stocks/validate:**
```json
// Request
["RELIANCE", "TCS", "FAKESTOCK"]

// Response
{
  "data": { "validTickers": ["RELIANCE","TCS"], "invalidTickers": ["FAKESTOCK"], "totalCount": 3, "validCount": 2, "invalidCount": 1 },
  "status": "SUCCESS",
  "statusCode": 200
}
```

---

## US5 — Upload Portfolio (Excel)

**Goal:** Authenticated user uploads an Excel file with stock holdings.

**Flow:** Upload → Parse (Apache POI) → Validate → Preview → User consent → Save

**API Endpoints:**
```
POST /api/v1/portfolio/upload/preview   → Step 1: parse + preview
POST /api/v1/portfolio/upload/confirm   → Step 2: confirm + save
Authorization: Bearer <token>
```

**Excel Format:**
| Stock Symbol | Company Name | Quantity | Buy Price |
|---|---|---|---|
| RELIANCE | Reliance Industries | 10 | 2800.00 |

**Preview Response:**
```json
{
  "data": {
    "newStocks": [{ "stockSymbol": "WIPRO", "quantity": 20, "buyPrice": 450.0 }],
    "conflictStocks": [{ "stockSymbol": "TCS", "quantity": 5, "buyPrice": 3900.0 }],
    "invalidStocks": ["FAKESTOCK"],
    "totalRows": 3, "newCount": 1, "conflictCount": 1, "invalidCount": 1
  },
  "status": "SUCCESS"
}
```

**Confirm Request / Response:**
```json
// Request
{ "newStocks": [{ "stockSymbol": "WIPRO", "quantity": 20, "buyPrice": 450.0 }],
  "updateStocks": [{ "stockSymbol": "TCS", "quantity": 5, "buyPrice": 3900.0 }] }

// Response
{ "msg": "Upload complete. Added: 1, Updated: 1, Skipped: 0",
  "data": { "addedCount": 1, "updatedCount": 1, "skippedCount": 0 },
  "status": "SUCCESS", "statusCode": 200 }
```

---

## US6 — Create Portfolio (UI Form)

**Goal:** Authenticated user adds stocks one by one via UI form. Valuation computed via Stream API.

**Key Concepts:** Stream API, JPA persistence, stock master validation

**API Endpoints:**
```
POST /api/v1/portfolio              → Add one stock
GET  /api/v1/portfolio/valuation    → Get portfolio with valuation
Authorization: Bearer <token>
```

**POST /api/v1/portfolio Request:**
```json
{ "stockSymbol": "RELIANCE", "companyName": "Reliance Industries", "quantity": 10, "buyPrice": 2800.00 }
```

**POST Response — Success (201):**
```json
{
  "msg": "Stock 'RELIANCE' added to portfolio successfully.",
  "data": { "id": 1, "stockSymbol": "RELIANCE", "quantity": 10, "buyPrice": 2800.0,
            "totalInvested": 28000.0, "currentValue": 28000.0, "profitLoss": 0.0, "profitLossPercent": 0.0 },
  "status": "SUCCESS",
  "statusCode": 201
}
```

**GET /api/v1/portfolio/valuation Response:**
```json
{
  "data": {
    "holdings": [
      { "stockSymbol": "RELIANCE", "quantity": 10, "buyPrice": 2800.0, "currentPrice": 3000.0,
        "totalInvested": 28000.0, "currentValue": 30000.0, "profitLoss": 2000.0, "profitLossPercent": 7.14 }
    ],
    "totalInvested": 28000.0, "totalCurrentValue": 30000.0,
    "totalProfitLoss": 2000.0, "totalProfitLossPercent": 7.14, "totalStocks": 1
  },
  "status": "SUCCESS", "statusCode": 200
}
```

---

## US7 — Manage Portfolio (Update / Delete)

**Goal:** Authenticated user views, updates, and deletes portfolio stocks with `@Transactional` rollback support.

**Actor:** Authenticated User

**Flow:**
1. View full portfolio with valuation
2. Update quantity or buy price of a stock (PUT)
3. Delete one stock
4. Delete all stocks

**Key Concepts:** `@Transactional(rollbackFor = Exception.class)`, `PUT` HTTP method, Stream API

**API Endpoints:**
```
GET    /api/v1/portfolio        → View full portfolio
PUT    /api/v1/portfolio/{id}   → Update one stock
DELETE /api/v1/portfolio/{id}   → Delete one stock
DELETE /api/v1/portfolio        → Delete all stocks
Authorization: Bearer <token>
```

**GET /api/v1/portfolio Response:**
```json
{
  "msg": "Portfolio fetched successfully.",
  "data": {
    "holdings": [
      { "id": 1, "stockSymbol": "RELIANCE", "quantity": 10, "buyPrice": 2800.0,
        "currentPrice": 3000.0, "totalInvested": 28000.0, "currentValue": 30000.0,
        "profitLoss": 2000.0, "profitLossPercent": 7.14 }
    ],
    "totalInvested": 28000.0, "totalCurrentValue": 30000.0,
    "totalProfitLoss": 2000.0, "totalProfitLossPercent": 7.14, "totalStocks": 1
  },
  "status": "SUCCESS", "statusCode": 200
}
```

**PUT /api/v1/portfolio/1 Request:**
```json
{ "quantity": 20, "buyPrice": 2500.00 }
```

**PUT Response — Success (200):**
```json
{
  "msg": "Stock 'RELIANCE' updated successfully.",
  "data": { "id": 1, "stockSymbol": "RELIANCE", "quantity": 20, "buyPrice": 2500.0,
            "totalInvested": 50000.0, "currentValue": 60000.0,
            "profitLoss": 10000.0, "profitLossPercent": 20.0 },
  "status": "SUCCESS", "statusCode": 200
}
```

**DELETE /api/v1/portfolio/1 Response:**
```json
{ "msg": "Stock 'RELIANCE' deleted successfully.", "status": "SUCCESS", "statusCode": 200 }
```

**DELETE /api/v1/portfolio (all) Response:**
```json
{ "msg": "All 3 stocks deleted successfully.", "status": "SUCCESS", "statusCode": 200 }
```

**Failure Responses:**
```json
{ "msg": "Portfolio record not found.", "status": "FAILED", "statusCode": 400 }
{ "msg": "Provide at least quantity or buy price to update.", "status": "FAILED", "statusCode": 400 }
{ "msg": "No portfolio records found to delete.", "status": "FAILED", "statusCode": 400 }
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

# 3. Run the application
mvn spring-boot:run

# 4. Run tests
mvn test

# 5. Code coverage report
mvn verify
# Report: target/site/jacoco/index.html
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
└── feature/US7-manage-portfolio
```

---

## 📬 Postman Collection Order

```
1.  POST   /api/v1/auth/register                 → Register user
2.  POST   /api/v1/auth/login                    → Login + get token
3.  GET    /api/v1/home                          → Home page
4.  GET    /api/v1/stocks                        → Load stock master
5.  GET    /api/v1/stocks/search?q=tata          → Search stocks
6.  POST   /api/v1/stocks/validate               → Validate tickers
7.  POST   /api/v1/portfolio/upload/preview      → Upload Excel preview
8.  POST   /api/v1/portfolio/upload/confirm      → Confirm Excel upload
9.  POST   /api/v1/portfolio                     → Add stock via form
10. GET    /api/v1/portfolio/valuation           → Portfolio valuation
11. GET    /api/v1/portfolio                     → View full portfolio
12. PUT    /api/v1/portfolio/{id}                → Update one stock
13. DELETE /api/v1/portfolio/{id}                → Delete one stock
14. DELETE /api/v1/portfolio                     → Delete all stocks
```
