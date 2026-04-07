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
│   │   │   │   ├── PortfolioController.java         ← US6
│   │   │   │   └── PortfolioUploadController.java
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
│   │   │   │   ├── CreatePortfolioRequestDto.java   ← US6
│   │   │   │   ├── PortfolioItemDto.java             ← US6
│   │   │   │   ├── PortfolioValuationDto.java        ← US6
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
│   │   │   │   ├── PortfolioService.java             ← US6
│   │   │   │   └── PortfolioUploadService.java
│   │   │   ├── serviceImpl/
│   │   │   │   ├── UserRegistrationServiceImpl.java
│   │   │   │   ├── UserLoginServiceImpl.java
│   │   │   │   ├── HomeServiceImpl.java
│   │   │   │   ├── StockServiceImpl.java
│   │   │   │   ├── StockMasterServiceImpl.java
│   │   │   │   ├── PortfolioServiceImpl.java         ← US6
│   │   │   │   └── PortfolioUploadServiceImpl.java
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
│           │   ├── PortfolioServiceImplTest.java      ← US6
│           │   └── PortfolioUploadServiceImplTest.java
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
| **US7** | Alert Setting | 🔲 Pending | - |
| **US8** | Monitor Portfolio | 🔲 Pending | - |
| **US9** | Notifications | 🔲 Pending | - |
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
  "dataList": null,
  "status": "SUCCESS",
  "statusCode": 201
}
```

**Response — Failure (400):**
```json
{
  "msg": "Invalid email format.",
  "data": null,
  "dataList": null,
  "status": "FAILED",
  "statusCode": 400
}
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
5. Token returned in response

**API Endpoint:**
```
POST /api/v1/auth/login
Content-Type: application/json
```

**Request:**
```json
{
  "username": "johndoe",
  "password": "Secret@123"
}
```

**Response — Success (200):**
```json
{
  "msg": "Login successful.",
  "data": {
    "username": "johndoe",
    "email": "john@example.com",
    "token": "eyJhbGciOiJIUzI1NiJ9.xxxxx"
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

**Response — Failure (401):**
```json
{
  "msg": "Invalid username or password.",
  "data": null,
  "status": "FAILED",
  "statusCode": 401
}
```

---

## US3 — Home Page with NSE Ticker

**Goal:** Authenticated user views menu options and NSE Top 50 stock ticker.

**Actor:** Authenticated User

**Flow:**
1. User lands on home page after login
2. Menu options returned
3. NSE Top 50 prices fetched via Alpha Vantage API

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

**Actor:** System + Authenticated User

**API Endpoints:**
```
GET  /api/v1/stocks              → Load all stocks
GET  /api/v1/stocks/search?q=    → Search by ticker or company name
POST /api/v1/stocks/validate     → Validate tickers from file upload
```

**GET /api/v1/stocks Response:**
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

**POST /api/v1/stocks/validate Request / Response:**
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

**Goal:** Authenticated user uploads an Excel file with stock holdings. New stocks added, existing updated with user consent.

**Actor:** Authenticated User

**Flow:**
1. Upload `.xls`/`.xlsx` file
2. Apache POI parses file
3. Each ticker validated against stock master
4. Preview: new / conflict / invalid
5. User consents → data saved

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

**Step 1 Preview Response:**
```json
{
  "data": {
    "newStocks": [{ "stockSymbol": "WIPRO", "quantity": 20, "buyPrice": 450.0 }],
    "conflictStocks": [{ "stockSymbol": "TCS", "quantity": 5, "buyPrice": 3900.0 }],
    "invalidStocks": ["FAKESTOCK"],
    "totalRows": 3, "newCount": 1, "conflictCount": 1, "invalidCount": 1
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

**Step 2 Confirm Request / Response:**
```json
// Request
{
  "newStocks": [{ "stockSymbol": "WIPRO", "quantity": 20, "buyPrice": 450.0 }],
  "updateStocks": [{ "stockSymbol": "TCS", "quantity": 5, "buyPrice": 3900.0 }]
}

// Response
{
  "msg": "Upload complete. Added: 1, Updated: 1, Skipped: 0",
  "data": { "addedCount": 1, "updatedCount": 1, "skippedCount": 0 },
  "status": "SUCCESS",
  "statusCode": 200
}
```

---

## US6 — Create Portfolio (UI Form)

**Goal:** Authenticated user adds stocks one by one via UI form. Portfolio valuation shown with profit/loss computed using Stream API.

**Actor:** Authenticated User

**Flow:**
1. User selects valid stock from master list
2. Enters quantity and buy price
3. Backend validates stock symbol against master table
4. Stock saved to portfolio table
5. Portfolio valuation returned (Stream API computes totals)

**Key Concepts:** Stream API for valuation, JPA persistence, stock master validation

**API Endpoints:**
```
POST /api/v1/portfolio              → Add one stock
GET  /api/v1/portfolio/valuation    → Get portfolio with valuation
Authorization: Bearer <token>
```

**POST /api/v1/portfolio Request:**
```json
{
  "stockSymbol": "RELIANCE",
  "companyName": "Reliance Industries",
  "quantity": 10,
  "buyPrice": 2800.00
}
```

**POST /api/v1/portfolio Response — Success (201):**
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

**GET /api/v1/portfolio/valuation Response:**
```json
{
  "msg": "Portfolio valuation fetched successfully.",
  "data": {
    "holdings": [
      {
        "id": 1, "stockSymbol": "RELIANCE", "companyName": "Reliance Industries",
        "quantity": 10, "buyPrice": 2800.0, "currentPrice": 3000.0,
        "totalInvested": 28000.0, "currentValue": 30000.0,
        "profitLoss": 2000.0, "profitLossPercent": 7.14
      },
      {
        "id": 2, "stockSymbol": "TCS", "companyName": "Tata Consultancy Services",
        "quantity": 5, "buyPrice": 3900.0, "currentPrice": 4000.0,
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

**Response — Failure (400):**
```json
{
  "msg": "Stock 'FAKESTOCK' is not valid. Please select from the stock master list.",
  "data": null,
  "status": "FAILED",
  "statusCode": 400
}
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
└── feature/US6-create-portfolio
```

---

## 📬 Postman Collection Order

```
1.  POST /api/v1/auth/register                  → Register user
2.  POST /api/v1/auth/login                     → Login + get token
3.  GET  /api/v1/home                           → Home page
4.  GET  /api/v1/stocks                         → Load stock master
5.  GET  /api/v1/stocks/search?q=tata           → Search stocks
6.  POST /api/v1/stocks/validate                → Validate tickers
7.  POST /api/v1/portfolio/upload/preview       → Upload Excel preview
8.  POST /api/v1/portfolio/upload/confirm       → Confirm Excel upload
9.  POST /api/v1/portfolio                      → Add stock via form
10. GET  /api/v1/portfolio/valuation            → Portfolio valuation
```
