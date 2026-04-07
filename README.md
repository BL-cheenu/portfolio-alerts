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
- Upload and manage their stock portfolio via Excel files
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
│   │   │   │   └── SecurityConfig.java          # BCrypt + JWT Filter
│   │   │   ├── controller/
│   │   │   │   ├── UserRegistrationController.java
│   │   │   │   ├── UserLoginController.java
│   │   │   │   ├── HomeController.java
│   │   │   │   ├── StockMasterController.java
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
│   │   │   │   └── PortfolioUploadService.java
│   │   │   ├── serviceImpl/
│   │   │   │   ├── UserRegistrationServiceImpl.java
│   │   │   │   ├── UserLoginServiceImpl.java
│   │   │   │   ├── HomeServiceImpl.java
│   │   │   │   ├── StockServiceImpl.java
│   │   │   │   ├── StockMasterServiceImpl.java
│   │   │   │   └── PortfolioUploadServiceImpl.java
│   │   │   └── utils/
│   │   │       ├── UserInputValidator.java
│   │   │       ├── JwtUtil.java
│   │   │       ├── ExcelParserUtil.java
│   │   │       └── NseTop50Symbols.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql                         # NSE Top 50 seed data
│   └── test/
│       └── java/com/ch/
│           ├── utils/
│           │   └── UserInputValidatorTest.java
│           ├── serviceImpl/
│           │   ├── UserRegistrationServiceImplTest.java
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
| `portfolio` | User's stock holdings |

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
| **US6** | Alert Setting | 🔲 Pending | - |
| **US7** | Monitor Portfolio | 🔲 Pending | - |
| **US8** | Portfolio Modify / Delete | 🔲 Pending | - |
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

---

## US1 — User Registration

**Goal:** Allow a new user to register securely with encrypted credentials stored in MySQL.

**Actor:** End User (Investor)

**Flow:**
1. User submits Name, Username, Email, Password
2. Backend validates email format and password rules
3. Password encrypted with BCrypt (strength 12)
4. User entity persisted via JPA
5. Success response returned

**Password Rules:**
- Minimum 8 characters
- At least 1 uppercase, 1 lowercase
- At least 1 digit
- At least 1 special character from `@#$%^*-_`

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
  "data": {
    "name": "John Doe",
    "username": "johndoe",
    "email": "john@example.com"
  },
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

**Goal:** Allow a registered user to login and receive a JWT token for authenticated access.

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
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIn0.xxxxx"
  },
  "dataList": null,
  "status": "SUCCESS",
  "statusCode": 200
}
```

**Response — Failure (401):**
```json
{
  "msg": "Invalid username or password.",
  "data": null,
  "dataList": null,
  "status": "FAILED",
  "statusCode": 401
}
```

---

## US3 — Home Page with NSE Ticker

**Goal:** Authenticated user views menu options and NSE Top 50 real-time stock ticker.

**Actor:** Authenticated User

**Flow:**
1. User lands on home page after login
2. Backend returns menu options
3. NSE Top 50 prices fetched via Alpha Vantage API
4. Ticker displayed at bottom of UI

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
  "dataList": null,
  "status": "SUCCESS",
  "statusCode": 200
}
```

---

## US4 — Stock Master Table

**Goal:** System stores NSE Top 50 stocks (company name + ticker) as master data. Used for validation across all screens.

**Actor:** System + Authenticated User

**Flow:**
1. NSE Top 50 stocks auto-loaded via `data.sql` on startup
2. Frontend loads full list on app initialisation
3. User can search by ticker or company name
4. File upload tickers cross-checked against master

**API Endpoints:**

```
GET  /api/v1/stocks              → Load all stocks
GET  /api/v1/stocks/search?q=    → Search stocks
POST /api/v1/stocks/validate     → Validate tickers
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

**GET /api/v1/stocks/search?q=tata Response:**
```json
{
  "dataList": [
    { "companyName": "Tata Consultancy Services", "tickerSymbol": "TCS", "exchange": "NSE" },
    { "companyName": "Tata Motors Ltd", "tickerSymbol": "TATAMOTORS", "exchange": "NSE" }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

**POST /api/v1/stocks/validate Request:**
```json
["RELIANCE", "TCS", "FAKESTOCK"]
```
**Response:**
```json
{
  "msg": "1 invalid ticker(s) found.",
  "data": {
    "validTickers": ["RELIANCE", "TCS"],
    "invalidTickers": ["FAKESTOCK"],
    "totalCount": 3,
    "validCount": 2,
    "invalidCount": 1
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

---

## US5 — Upload Portfolio (Excel)

**Goal:** Authenticated user uploads an Excel file with stock holdings. New stocks added, existing stocks updated with user consent.

**Actor:** Authenticated User

**Flow:**
1. User uploads `.xls` / `.xlsx` file
2. Apache POI parses the file
3. Each ticker validated against stock master (US4)
4. Preview shown: new / conflict / invalid
5. User consents to update conflicts
6. Data saved to `portfolio` table

**Excel Format:**
| Stock Symbol | Company Name | Quantity | Buy Price |
|---|---|---|---|
| RELIANCE | Reliance Industries | 10 | 2800.00 |

**API Endpoints:**
```
POST /api/v1/portfolio/upload/preview   → Step 1: parse + preview
POST /api/v1/portfolio/upload/confirm   → Step 2: confirm + save
Authorization: Bearer <token>
```

**Step 1 — Preview Response:**
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

**Step 2 — Confirm Request:**
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

**Step 2 — Confirm Response:**
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

## ▶️ How to Run

```bash
# 1. Create MySQL database
mysql -u root -p
CREATE DATABASE investor_db;

# 2. Update application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/investor_db
spring.datasource.username=root
spring.datasource.password=your_password

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
└── feature/US5-portfolio-upload
```

---

## 📬 Postman Collection Order

```
1. POST /api/v1/auth/register       → Create user
2. POST /api/v1/auth/login          → Get JWT token
3. GET  /api/v1/home                → Home page (use token)
4. GET  /api/v1/stocks              → Stock master list
5. GET  /api/v1/stocks/search?q=    → Search stocks
6. POST /api/v1/stocks/validate     → Validate tickers
7. POST /api/v1/portfolio/upload/preview  → Upload Excel preview
8. POST /api/v1/portfolio/upload/confirm  → Confirm upload
```
