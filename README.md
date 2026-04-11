# 📈 Portfolio Alerts App — Backend

> A Spring Boot REST API for investment portfolio management with real-time Kafka monitoring, RabbitMQ alert processing, and email notifications.

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
| **Message Queues** | Apache Kafka + RabbitMQ |
| **Build Tool** | Maven |

---

## 📋 Description

Portfolio Alerts App is a complete backend REST API that allows authenticated investors to:
- Register and login securely with BCrypt + JWT
- View NSE Top 50 live stock ticker on home page
- Upload portfolio via Excel or add stocks one by one via UI form
- Validate stocks against NSE master list
- View live portfolio valuation with profit/loss (Stream API)
- Manage portfolio — update quantity/price, delete one or all stocks
- Set upper and lower % threshold alerts per stock
- Monitor portfolio in real-time via Kafka price cache + threshold breach detection
- Receive automated HTML email alerts when price crosses threshold via RabbitMQ

---

## 🏗️ Project Structure

```
portfolio-alerts/
├── src/main/java/com/ch/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── KafkaConfig.java
│   │   └── RabbitMQConfig.java
│   ├── controller/
│   │   ├── UserRegistrationController.java
│   │   ├── UserLoginController.java
│   │   ├── HomeController.java
│   │   ├── StockMasterController.java
│   │   ├── PortfolioController.java
│   │   ├── PortfolioUploadController.java
│   │   ├── ManagePortfolioController.java
│   │   ├── AlertController.java
│   │   ├── MonitorController.java
│   │   └── AlertHistoryController.java
│   ├── customexception/
│   │   ├── UserRegistrationException.java
│   │   └── UserLoginException.java
│   ├── dto/
│   │   ├── CommonDto.java
│   │   ├── UserRegisterDto.java
│   │   ├── LoginRequestDto.java
│   │   ├── LoginResponseDto.java
│   │   ├── HomeResponseDto.java
│   │   ├── StockDto.java
│   │   ├── StockMasterDto.java
│   │   ├── StockValidationDto.java
│   │   ├── CreatePortfolioRequestDto.java
│   │   ├── PortfolioItemDto.java
│   │   ├── PortfolioValuationDto.java
│   │   ├── UpdatePortfolioRequestDto.java
│   │   ├── AlertRequestDto.java
│   │   ├── AlertResponseDto.java
│   │   ├── MonitorStockDto.java
│   │   ├── MonitorPortfolioDto.java
│   │   ├── AlertHistoryDto.java
│   │   ├── UploadRowDto.java
│   │   ├── UploadPreviewDto.java
│   │   ├── UploadConfirmDto.java
│   │   └── UploadResultDto.java
│   ├── entity/
│   │   ├── UserEntity.java
│   │   ├── StockEntity.java
│   │   ├── PortfolioEntity.java
│   │   ├── AlertEntity.java
│   │   └── AlertHistoryEntity.java
│   ├── kafka/
│   │   ├── StockPriceMessage.java
│   │   ├── StockPriceCache.java
│   │   ├── StockPriceProducer.java
│   │   ├── StockPriceConsumer.java
│   │   └── StockPriceScheduler.java
│   ├── rabbitmq/
│   │   ├── AlertEmailMessage.java
│   │   ├── AlertGenerator.java
│   │   └── AlertConsumer.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── StockRepository.java
│   │   ├── PortfolioRepository.java
│   │   ├── AlertRepository.java
│   │   └── AlertHistoryRepository.java
│   ├── service/
│   │   ├── UserRegistrationService.java
│   │   ├── UserLoginService.java
│   │   ├── HomeService.java
│   │   ├── StockService.java
│   │   ├── StockMasterService.java
│   │   ├── PortfolioService.java
│   │   ├── PortfolioUploadService.java
│   │   ├── ManagePortfolioService.java
│   │   ├── AlertService.java
│   │   ├── MonitorService.java
│   │   ├── AlertEmailService.java
│   │   └── AlertHistoryService.java
│   ├── serviceImpl/
│   │   ├── UserRegistrationServiceImpl.java
│   │   ├── UserLoginServiceImpl.java
│   │   ├── HomeServiceImpl.java
│   │   ├── StockServiceImpl.java
│   │   ├── StockMasterServiceImpl.java
│   │   ├── PortfolioServiceImpl.java
│   │   ├── PortfolioUploadServiceImpl.java
│   │   ├── ManagePortfolioServiceImpl.java
│   │   ├── AlertServiceImpl.java
│   │   ├── MonitorServiceImpl.java
│   │   └── AlertHistoryServiceImpl.java
│   └── utils/
│       ├── UserInputValidator.java
│       ├── JwtUtil.java
│       ├── JwtAuthFilter.java
│       ├── ExcelParserUtil.java
│       └── NseTop50Symbols.java
├── src/main/resources/
│   ├── application.properties
│   └── data.sql
├── src/test/java/com/ch/
│   ├── utils/UserInputValidatorTest.java
│   └── serviceImpl/
│       ├── UserRegistrationServiceImplTest.java
│       ├── PortfolioServiceImplTest.java
│       ├── PortfolioUploadServiceImplTest.java
│       ├── ManagePortfolioServiceImplTest.java
│       ├── AlertServiceImplTest.java
│       ├── MonitorServiceImplTest.java
│       └── AlertGeneratorTest.java
├── pom.xml
└── README.md
```

---

## 🗄️ Database Tables

| Table | Description |
|---|---|
| `users` | Registered user credentials |
| `stocks` | NSE Top 50 master (seeded via data.sql) |
| `portfolio` | User's stock holdings |
| `alerts` | Upper/lower % threshold settings per stock |
| `alert_history` | All email alerts sent — for UI display |

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
| **US9** | Real-time Portfolio Monitor | ✅ Completed | `feature/US9-realtime-monitor` |
| **US10** | Send Alert Email via RabbitMQ | ✅ Completed | `feature/US10-alert-email` |

---

## 📦 Dependencies (pom.xml)

```xml
<!-- Spring Boot Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Spring Security Crypto (BCrypt) -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Dev Tools -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- Apache POI (Excel) -->
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

<!-- Spring Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<!-- RabbitMQ -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>

<!-- Spring Mail -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.8.0</version>
    <scope>test</scope>
</dependency>

```

---

## ▶️ How to Run

```bash
# 1. Start Kafka
docker run -d --name kafka -p 9092:9092 apache/kafka:latest

# 2. Start RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management

# 3. Create MySQL DB
mysql -u root -p
CREATE DATABASE investor_db;

# 4. Update application.properties
spring.datasource.password=your_password
stock.api.key=YOUR_ALPHA_VANTAGE_KEY
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password

# 5. Run
mvn spring-boot:run

# 6. Test
mvn test

# 7. Coverage
mvn verify
# Report: target/site/jacoco/index.html
```

---

## 📬 Postman Collection Order

```
1.  POST   /api/v1/auth/register
2.  POST   /api/v1/auth/login
3.  GET    /api/v1/home
4.  GET    /api/v1/stocks
5.  GET    /api/v1/stocks/search?q=tata
6.  POST   /api/v1/stocks/validate
7.  POST   /api/v1/portfolio/upload/preview
8.  POST   /api/v1/portfolio/upload/confirm
9.  POST   /api/v1/portfolio
10. GET    /api/v1/portfolio/valuation
11. GET    /api/v1/portfolio
12. PUT    /api/v1/portfolio/{id}
13. DELETE /api/v1/portfolio/{id}
14. DELETE /api/v1/portfolio
15. POST   /api/v1/alerts
16. PUT    /api/v1/alerts/{id}
17. GET    /api/v1/alerts
18. GET    /api/v1/alerts/stock/RELIANCE
19. DELETE /api/v1/alerts/{id}
20. GET    /api/v1/monitor
21. GET    /api/v1/monitor/RELIANCE
22. GET    /api/v1/alert-history
23. GET    /api/v1/alert-history/stock/RELIANCE
```

---

# 📖 User Story Details

---

## US1 — User Registration

**Goal:** New user registers securely with encrypted credentials stored in MySQL.

**Actor:** End User (Investor)

**Flow:**
1. User enters Name, Email, Username, Password
2. Java 8 Predicate validates email format and password rules
3. BCrypt (strength 12) encrypts password
4. User entity persisted via JPA
5. Response returned

**Password Rules:** Min 8 chars · 1 uppercase · 1 lowercase · 1 digit · 1 special char (`@#$%^*-_`)

**Concepts:** Java 8 Predicate · BCrypt · JPA · SLF4J Logger

**API:** `POST /api/v1/auth/register`

**✅ Success Request:**
```json
{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "Secret@123"
}
```

**✅ Success Response (201):**
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

**❌ Invalid Email (400):**
```json
{
  "msg": "Invalid email format.",
  "data": null,
  "dataList": null,
  "status": "FAILED",
  "statusCode": 400
}
```

**❌ Weak Password (400):**
```json
{
  "msg": "Password must contain at least one uppercase letter.",
  "data": null,
  "dataList": null,
  "status": "FAILED",
  "statusCode": 400
}
```

**❌ Duplicate Username (400):**
```json
{
  "msg": "Username 'johndoe' is already taken.",
  "data": null,
  "dataList": null,
  "status": "FAILED",
  "statusCode": 400
}
```

**❌ Duplicate Email (400):**
```json
{
  "msg": "Email 'john@example.com' is already registered.",
  "data": null,
  "dataList": null,
  "status": "FAILED",
  "statusCode": 400
}
```

---

## US2 — User Login & Authentication

**Goal:** Registered user logs in and receives a JWT token for authenticated access.

**Actor:** Registered User

**Flow:**
1. User submits username and password
2. Backend fetches user via `Optional<UserEntity>`
3. BCrypt password verification
4. JWT token generated (24hr, HS256)
5. Token returned in response

**Concepts:** Optional · JWT · BCrypt matches() · SLF4J Logger

**API:** `POST /api/v1/auth/login`

**✅ Success Request:**
```json
{
  "username": "johndoe",
  "password": "Secret@123"
}
```

**✅ Success Response (200):**
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

**❌ Wrong Password (401):**
```json
{
  "msg": "Invalid username or password.",
  "data": null,
  "dataList": null,
  "status": "FAILED",
  "statusCode": 401
}
```

**❌ Blank Username (401):**
```json
{
  "msg": "Username must not be blank.",
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
1. User lands on home page after login (JWT validated by JwtAuthFilter)
2. Menu options returned
3. NSE Top 50 prices fetched via Alpha Vantage API

**Concepts:** JwtAuthFilter · RestTemplate · Alpha Vantage API · SLF4J Logger

**API:** `GET /api/v1/home`

**✅ Success Response (200):**
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
      },
      {
        "symbol": "TCS",
        "companyName": "Tata Consultancy Services",
        "currentPrice": 3920.15,
        "change": -8.75,
        "changePercent": -0.22
      }
    ]
  },
  "dataList": null,
  "status": "SUCCESS",
  "statusCode": 200
}
```

**❌ Missing Token (401):**
```json
{
  "msg": "Authorization token missing.",
  "status": "FAILED",
  "statusCode": 401
}
```

**❌ Invalid Token (401):**
```json
{
  "msg": "Invalid or expired token.",
  "status": "FAILED",
  "statusCode": 401
}
```

---

## US4 — Stock Master Table

**Goal:** System stores NSE Top 50 stocks as master data for validation across all screens.

**Actor:** System + Authenticated User

**Flow:**
1. NSE Top 50 stocks auto-loaded via `data.sql` on startup
2. Frontend loads full list on app initialisation
3. User searches by ticker or company name
4. File upload tickers cross-checked against master

**Concepts:** DB Seeding (data.sql) · JPA Repository · SLF4J Logger

**APIs:**
```
GET  /api/v1/stocks
GET  /api/v1/stocks/search?q=
POST /api/v1/stocks/validate
```

**✅ GET /api/v1/stocks Response (200):**
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

**✅ GET /api/v1/stocks/search?q=tata Response (200):**
```json
{
  "msg": "Stocks found.",
  "dataList": [
    { "companyName": "Tata Consultancy Services", "tickerSymbol": "TCS", "exchange": "NSE" },
    { "companyName": "Tata Motors Ltd", "tickerSymbol": "TATAMOTORS", "exchange": "NSE" },
    { "companyName": "Tata Steel Ltd", "tickerSymbol": "TATASTEEL", "exchange": "NSE" }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

**✅ POST /api/v1/stocks/validate Request:**
```json
["RELIANCE", "TCS", "FAKESTOCK", "WIPRO"]
```

**✅ POST /api/v1/stocks/validate Response (200):**
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

**❌ Empty Keyword (400):**
```json
{
  "msg": "Search keyword must not be blank.",
  "status": "FAILED",
  "statusCode": 400
}
```

---

## US5 — Upload Portfolio (Excel)

**Goal:** Authenticated user uploads Excel file with stock holdings. New added, existing updated with consent.

**Actor:** Authenticated User

**Flow:**
1. Upload `.xls` / `.xlsx` file
2. Apache POI parses each row
3. Each ticker validated against stock master
4. Preview: new / conflict / invalid
5. User consents → data saved to `portfolio` table

**Concepts:** Apache POI · Stock master validation · 2-Step API · SLF4J Logger

**Excel Format:**
| Stock Symbol | Company Name | Quantity | Buy Price |
|---|---|---|---|
| RELIANCE | Reliance Industries | 10 | 2800.00 |
| TCS | Tata Consultancy | 5 | 3900.00 |

**APIs:**
```
POST /api/v1/portfolio/upload/preview   → Step 1
POST /api/v1/portfolio/upload/confirm   → Step 2
Body (preview): form-data → Key: file, Type: File
```

**✅ Step 1 Preview Response (200):**
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

**✅ Step 2 Confirm Request:**
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

**✅ Step 2 Confirm Response (200):**
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

**❌ Empty File (400):**
```json
{
  "msg": "File must not be empty.",
  "status": "FAILED",
  "statusCode": 400
}
```

---

## US6 — Create Portfolio (UI Form)

**Goal:** Authenticated user adds stocks one by one via UI form. Valuation computed via Stream API.

**Actor:** Authenticated User

**Flow:**
1. User selects valid stock from master list
2. Enters quantity and buy price
3. Backend validates stock symbol against master table
4. Stock saved to `portfolio` table
5. Portfolio valuation returned — profit/loss via Stream API

**Concepts:** Stream API · Stock master validation · JPA persistence · SLF4J Logger

**APIs:**
```
POST /api/v1/portfolio
GET  /api/v1/portfolio/valuation
```

**✅ POST /api/v1/portfolio Request:**
```json
{
  "stockSymbol": "RELIANCE",
  "companyName": "Reliance Industries",
  "quantity": 10,
  "buyPrice": 2800.00
}
```

**✅ POST /api/v1/portfolio Response (201):**
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

**✅ GET /api/v1/portfolio/valuation Response (200):**
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

**❌ Invalid Stock (400):**
```json
{
  "msg": "Stock 'FAKESTOCK' is not valid. Please select from the stock master list.",
  "status": "FAILED",
  "statusCode": 400
}
```

**❌ Duplicate Stock (400):**
```json
{
  "msg": "Stock 'RELIANCE' already exists in your portfolio. Use update instead.",
  "status": "FAILED",
  "statusCode": 400
}
```

---

## US7 — Manage Portfolio (Update / Delete)

**Goal:** Authenticated user views, updates, and deletes portfolio stocks with `@Transactional` rollback.

**Actor:** Authenticated User

**Flow:**
1. View full portfolio with valuation
2. Update quantity or buy price of a stock
3. Delete one specific stock
4. Or delete all stocks at once
5. All DB operations wrapped in `@Transactional` — auto rollback on failure

**Concepts:** `@Transactional(rollbackFor = Exception.class)` · PUT HTTP method · Stream API · SLF4J Logger

**APIs:**
```
GET    /api/v1/portfolio
PUT    /api/v1/portfolio/{id}
DELETE /api/v1/portfolio/{id}
DELETE /api/v1/portfolio
```

**✅ GET /api/v1/portfolio Response (200):**
```json
{
  "msg": "Portfolio fetched successfully.",
  "data": {
    "holdings": [
      {
        "id": 1, "stockSymbol": "RELIANCE",
        "quantity": 10, "buyPrice": 2800.0, "currentPrice": 3000.0,
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

**✅ PUT /api/v1/portfolio/1 Request:**
```json
{ "quantity": 20, "buyPrice": 2500.00 }
```

**✅ PUT /api/v1/portfolio/1 Response (200):**
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

**✅ DELETE /api/v1/portfolio/1 Response (200):**
```json
{
  "msg": "Stock 'RELIANCE' deleted successfully.",
  "status": "SUCCESS",
  "statusCode": 200
}
```

**✅ DELETE /api/v1/portfolio Response (200):**
```json
{
  "msg": "All 3 stocks deleted successfully.",
  "status": "SUCCESS",
  "statusCode": 200
}
```

**❌ Portfolio Not Found (400):**
```json
{
  "msg": "Portfolio record not found.",
  "status": "FAILED",
  "statusCode": 400
}
```

**❌ No Fields to Update (400):**
```json
{
  "msg": "Provide at least quantity or buy price to update.",
  "status": "FAILED",
  "statusCode": 400
}
```

---

## US8 — Alert Threshold Setting

**Goal:** Authenticated user sets upper and lower % thresholds for stock price alerts.

**Actor:** Authenticated User

**Flow:**
1. User selects a stock and sets upper % and lower % threshold
2. Backend validates stock against master
3. Thresholds saved to `alerts` table
4. Alert prices calculated: `buyPrice ± (buyPrice × threshold%)`
5. User can update or delete thresholds

**Concepts:** Alert price formula · Stock master validation · Unique constraint · SLF4J Logger

**Alert Price Formula:**
```
upperAlertPrice = buyPrice + (buyPrice × upperThreshold / 100)
lowerAlertPrice = buyPrice - (buyPrice × lowerThreshold / 100)

Example: buyPrice = ₹2800, upper = 10%, lower = 5%
  upperAlertPrice = 2800 + 280 = ₹3080
  lowerAlertPrice = 2800 - 140 = ₹2660
```

**APIs:**
```
POST   /api/v1/alerts
PUT    /api/v1/alerts/{id}
GET    /api/v1/alerts
GET    /api/v1/alerts/stock/{symbol}
DELETE /api/v1/alerts/{id}
```

**✅ POST /api/v1/alerts Request:**
```json
{
  "stockSymbol": "RELIANCE",
  "companyName": "Reliance Industries",
  "upperThreshold": 10.0,
  "lowerThreshold": 5.0
}
```

**✅ POST /api/v1/alerts Response (201):**
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

**✅ PUT /api/v1/alerts/1 Request:**
```json
{ "upperThreshold": 15.0 }
```

**✅ PUT /api/v1/alerts/1 Response (200):**
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

**✅ GET /api/v1/alerts Response (200):**
```json
{
  "msg": "Alerts fetched successfully.",
  "dataList": [
    { "stockSymbol": "RELIANCE", "upperThreshold": 10.0, "lowerThreshold": 5.0, "buyPrice": 2800.0, "upperAlertPrice": 3080.0, "lowerAlertPrice": 2660.0, "isActive": true },
    { "stockSymbol": "TCS", "upperThreshold": 8.0, "lowerThreshold": 4.0, "buyPrice": 3900.0, "upperAlertPrice": 4212.0, "lowerAlertPrice": 3744.0, "isActive": true }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

**✅ DELETE /api/v1/alerts/1 Response (200):**
```json
{
  "msg": "Alert for 'RELIANCE' deleted successfully.",
  "status": "SUCCESS",
  "statusCode": 200
}
```

**❌ Duplicate Alert (400):**
```json
{
  "msg": "Alert already exists for 'RELIANCE'. Use update instead.",
  "status": "FAILED",
  "statusCode": 400
}
```

**❌ Invalid Stock (400):**
```json
{
  "msg": "Stock 'FAKESTOCK' is not valid.",
  "status": "FAILED",
  "statusCode": 400
}
```

**❌ Alert Not Found (400):**
```json
{
  "msg": "Alert not found.",
  "status": "FAILED",
  "statusCode": 400
}
```

---

## US9 — Real-time Portfolio Monitor

**Goal:** Authenticated user sees live portfolio gain/loss with Kafka-powered real-time prices and threshold breach indicators.

**Actor:** Authenticated User

**Flow:**
1. `StockPriceScheduler` fetches prices from Alpha Vantage every 60s
2. Publishes to Kafka `stock-prices` topic
3. `StockPriceConsumer` updates `StockPriceCache` (ConcurrentHashMap)
4. Monitor endpoint reads from cache
5. Stream API computes per-stock and overall gain/loss
6. Threshold breach checked against US8 alerts

**Concepts:** Apache Kafka · ConcurrentHashMap · Stream API · `@Scheduled` · `@Transactional(readOnly=true)` · SLF4J Logger

**Kafka Flow:**
```
Alpha Vantage API → StockPriceScheduler (@Scheduled 60s)
                         ↓
                   StockPriceProducer → stock-prices topic
                                              ↓
                                     StockPriceConsumer
                                              ↓
                                     StockPriceCache
                                              ↓
                                     MonitorServiceImpl (Stream API)
```

**APIs:**
```
GET /api/v1/monitor
GET /api/v1/monitor/{symbol}
```

**✅ GET /api/v1/monitor Response (200):**
```json
{
  "msg": "Portfolio monitoring data fetched successfully.",
  "data": {
    "stocks": [
      {
        "stockSymbol": "RELIANCE",
        "companyName": "Reliance Industries",
        "quantity": 10,
        "buyPrice": 2800.0,
        "currentPrice": 3100.0,
        "totalInvested": 28000.0,
        "currentValue": 31000.0,
        "gainLoss": 3000.0,
        "gainLossPercent": 10.71,
        "upperThreshold": 10.0,
        "lowerThreshold": 5.0,
        "upperAlertPrice": 3080.0,
        "lowerAlertPrice": 2660.0,
        "upperBreached": true,
        "lowerBreached": false,
        "alertStatus": "UPPER_BREACHED"
      },
      {
        "stockSymbol": "TCS",
        "quantity": 5,
        "buyPrice": 3900.0,
        "currentPrice": 3950.0,
        "totalInvested": 19500.0,
        "currentValue": 19750.0,
        "gainLoss": 250.0,
        "gainLossPercent": 1.28,
        "alertStatus": "NORMAL"
      }
    ],
    "totalInvested": 47500.0,
    "totalCurrentValue": 50750.0,
    "totalGainLoss": 3250.0,
    "totalGainLossPercent": 6.84,
    "totalStocks": 2,
    "upperBreachedCount": 1,
    "lowerBreachedCount": 0,
    "normalCount": 1,
    "lastUpdated": "2025-04-09T10:30:00"
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

**✅ GET /api/v1/monitor/RELIANCE Response (200):**
```json
{
  "msg": "Stock monitor data fetched.",
  "data": {
    "stockSymbol": "RELIANCE",
    "quantity": 10,
    "buyPrice": 2800.0,
    "currentPrice": 3100.0,
    "gainLoss": 3000.0,
    "gainLossPercent": 10.71,
    "upperAlertPrice": 3080.0,
    "lowerAlertPrice": 2660.0,
    "upperBreached": true,
    "alertStatus": "UPPER_BREACHED"
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

**❌ Stock Not in Portfolio (400):**
```json
{
  "msg": "Stock 'WIPRO' not found in portfolio.",
  "status": "FAILED",
  "statusCode": 400
}
```

**❌ Empty Portfolio (404):**
```json
{
  "msg": "No portfolio found.",
  "status": "FAILED",
  "statusCode": 404
}
```

**Alert Status Values:**
| Status | Meaning |
|---|---|
| `UPPER_BREACHED` | Current price ≥ upperAlertPrice |
| `LOWER_BREACHED` | Current price ≤ lowerAlertPrice |
| `NORMAL` | Within threshold range |
| `NO_ALERT_SET` | No US8 alert configured for this stock |

---

## US10 — Send Alert Email via RabbitMQ

**Goal:** System sends HTML email to user when stock price crosses threshold. Alert history stored in DB.

**Actor:** System (automated) + Authenticated User (receiver)

**Flow:**
1. `AlertGenerator` runs every 60s
2. Checks live prices from `StockPriceCache` vs US8 thresholds
3. If breached → serializes `AlertEmailMessage` as JSON String
4. Publishes to RabbitMQ `alert.exchange` → `alert.email.queue`
5. `AlertConsumer` receives → sends HTML email + saves to `alert_history`
6. On failure → message goes to Dead Letter Queue (DLQ)

**Concepts:** RabbitMQ Producer/Consumer · Dead Letter Queue · Spring Mail · SimpleMessageConverter · `@Scheduled` · SLF4J Logger

**RabbitMQ Flow:**
```
AlertGenerator (@Scheduled 60s)
      ↓ breach detected →
RabbitTemplate.convertAndSend (JSON String)
      ↓
alert.exchange → alert.email.queue
      ↓
AlertConsumer (@RabbitListener)
      ↓
AlertEmailService → HTML Email ✉️  +  AlertHistoryService → DB
      ↓ (on failure)
alert.email.dlq (Dead Letter Queue)
```

**APIs:**
```
GET /api/v1/alert-history
GET /api/v1/alert-history/stock/{symbol}
```

**✅ GET /api/v1/alert-history Response (200):**
```json
{
  "msg": "Alert history fetched successfully.",
  "dataList": [
    {
      "id": 1,
      "stockSymbol": "RELIANCE",
      "companyName": "Reliance Industries",
      "breachType": "UPPER_BREACHED",
      "buyPrice": 2800.0,
      "currentPrice": 3100.0,
      "alertPrice": 3080.0,
      "gainLoss": 3000.0,
      "gainLossPercent": 10.71,
      "emailSentTo": "john@example.com",
      "triggeredAt": "2025-04-09T10:30:00"
    },
    {
      "id": 2,
      "stockSymbol": "TCS",
      "companyName": "Tata Consultancy Services",
      "breachType": "LOWER_BREACHED",
      "buyPrice": 3900.0,
      "currentPrice": 3700.0,
      "alertPrice": 3744.0,
      "gainLoss": -1000.0,
      "gainLossPercent": -5.13,
      "emailSentTo": "john@example.com",
      "triggeredAt": "2025-04-09T11:00:00"
    }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

**✅ GET /api/v1/alert-history/stock/RELIANCE Response (200):**
```json
{
  "msg": "Alert history fetched.",
  "dataList": [
    {
      "stockSymbol": "RELIANCE",
      "breachType": "UPPER_BREACHED",
      "buyPrice": 2800.0,
      "currentPrice": 3100.0,
      "alertPrice": 3080.0,
      "gainLoss": 3000.0,
      "gainLossPercent": 10.71,
      "emailSentTo": "john@example.com",
      "triggeredAt": "2025-04-09T10:30:00"
    }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

**❌ No History Found (200 with empty list):**
```json
{
  "msg": "No alert history for stock 'WIPRO'.",
  "dataList": [],
  "status": "SUCCESS",
  "statusCode": 200
}
```
