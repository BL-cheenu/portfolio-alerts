# 📈 Portfolio Alerts App — Backend

> A Spring Boot REST API for an investment portfolio management system with real-time stock monitoring and Kafka-powered alert capabilities.

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
| **Message Queue** | Apache Kafka |
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
- Monitor portfolio in real-time using Kafka + Stream API with threshold breach detection

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
│   │   │   │   ├── AlertController.java
│   │   │   │   └── MonitorController.java             ← US9
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
│   │   │   │   ├── AlertRequestDto.java
│   │   │   │   ├── AlertResponseDto.java
│   │   │   │   ├── MonitorStockDto.java               ← US9
│   │   │   │   ├── MonitorPortfolioDto.java           ← US9
│   │   │   │   ├── UploadRowDto.java
│   │   │   │   ├── UploadPreviewDto.java
│   │   │   │   ├── UploadConfirmDto.java
│   │   │   │   └── UploadResultDto.java
│   │   │   ├── entity/
│   │   │   │   ├── UserEntity.java
│   │   │   │   ├── StockEntity.java
│   │   │   │   ├── PortfolioEntity.java
│   │   │   │   └── AlertEntity.java
│   │   │   ├── filter/
│   │   │   │   └── JwtAuthFilter.java
│   │   │   ├── kafka/                                 ← US9
│   │   │   │   ├── StockPriceMessage.java
│   │   │   │   ├── StockPriceCache.java
│   │   │   │   ├── StockPriceProducer.java
│   │   │   │   ├── StockPriceConsumer.java
│   │   │   │   └── StockPriceScheduler.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── StockRepository.java
│   │   │   │   ├── PortfolioRepository.java
│   │   │   │   └── AlertRepository.java
│   │   │   ├── service/
│   │   │   │   ├── UserRegistrationService.java
│   │   │   │   ├── UserLoginService.java
│   │   │   │   ├── HomeService.java
│   │   │   │   ├── StockService.java
│   │   │   │   ├── StockMasterService.java
│   │   │   │   ├── PortfolioService.java
│   │   │   │   ├── PortfolioUploadService.java
│   │   │   │   ├── ManagePortfolioService.java
│   │   │   │   ├── AlertService.java
│   │   │   │   └── MonitorService.java                ← US9
│   │   │   ├── serviceImpl/
│   │   │   │   ├── UserRegistrationServiceImpl.java
│   │   │   │   ├── UserLoginServiceImpl.java
│   │   │   │   ├── HomeServiceImpl.java
│   │   │   │   ├── StockServiceImpl.java
│   │   │   │   ├── StockMasterServiceImpl.java
│   │   │   │   ├── PortfolioServiceImpl.java
│   │   │   │   ├── PortfolioUploadServiceImpl.java
│   │   │   │   ├── ManagePortfolioServiceImpl.java
│   │   │   │   ├── AlertServiceImpl.java
│   │   │   │   └── MonitorServiceImpl.java            ← US9
│   │   │   └── utils/
│   │   │       ├── UserInputValidator.java
│   │   │       ├── JwtUtil.java
│   │   │       ├── ExcelParserUtil.java
│   │   │       └── NseTop50Symbols.java
│   │   └── resources/
│   │       ├── application.properties                 ← Kafka config added
│   │       └── data.sql
│   └── test/
│       └── java/com/ch/
│           ├── utils/UserInputValidatorTest.java
│           ├── serviceImpl/
│           │   ├── UserRegistrationServiceImplTest.java
│           │   ├── PortfolioServiceImplTest.java
│           │   ├── PortfolioUploadServiceImplTest.java
│           │   ├── ManagePortfolioServiceImplTest.java
│           │   ├── AlertServiceImplTest.java
│           │   └── MonitorServiceImplTest.java        ← US9
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
| Spring Kafka | - | Message queue |
| Lombok | - | Boilerplate reduction |
| JUnit 5 | 5.10.1 | Unit testing |
| Mockito | 5.8.0 | Mocking |
| JaCoCo | 0.8.11 | Code coverage (min 80%) |

---

## US1 — User Registration

**API:** `POST /api/v1/auth/register`

**Request:**
```json
{ "name": "John Doe", "username": "johndoe", "email": "john@example.com", "password": "Secret@123" }
```
**Response (201):**
```json
{ "msg": "User registered successfully.", "data": { "name": "John Doe", "username": "johndoe", "email": "john@example.com" }, "status": "SUCCESS", "statusCode": 201 }
```

---

## US2 — User Login & Authentication

**API:** `POST /api/v1/auth/login`

**Request:**
```json
{ "username": "johndoe", "password": "Secret@123" }
```
**Response (200):**
```json
{ "msg": "Login successful.", "data": { "username": "johndoe", "email": "john@example.com", "token": "eyJhbGciOiJIUzI1NiJ9.xxxxx" }, "status": "SUCCESS", "statusCode": 200 }
```

---

## US3 — Home Page with NSE Ticker

**API:** `GET /api/v1/home`

**Response (200):**
```json
{ "data": { "welcomeMessage": "Welcome to Portfolio Alerts App!", "username": "johndoe", "menuOptions": ["Portfolio Creation / Updation", "Alert Setting", "Monitor Portfolio"], "nseTop50Ticker": [{ "symbol": "RELIANCE", "currentPrice": 2845.50, "change": 12.30, "changePercent": 0.43 }] }, "status": "SUCCESS" }
```

---

## US4 — Stock Master Table

**APIs:**
```
GET  /api/v1/stocks
GET  /api/v1/stocks/search?q=tata
POST /api/v1/stocks/validate
```

---

## US5 — Upload Portfolio (Excel)

**APIs:**
```
POST /api/v1/portfolio/upload/preview
POST /api/v1/portfolio/upload/confirm
```

---

## US6 — Create Portfolio (UI Form)

**APIs:**
```
POST /api/v1/portfolio
GET  /api/v1/portfolio/valuation
```

**Add Request:**
```json
{ "stockSymbol": "RELIANCE", "companyName": "Reliance Industries", "quantity": 10, "buyPrice": 2800.00 }
```

---

## US7 — Manage Portfolio (Update / Delete)

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

---

## US8 — Alert Threshold Setting

**APIs:**
```
POST   /api/v1/alerts
PUT    /api/v1/alerts/{id}
GET    /api/v1/alerts
GET    /api/v1/alerts/stock/{symbol}
DELETE /api/v1/alerts/{id}
```

**Set Alert Request:**
```json
{ "stockSymbol": "RELIANCE", "upperThreshold": 10.0, "lowerThreshold": 5.0 }
```

**Response (201):**
```json
{ "data": { "stockSymbol": "RELIANCE", "upperThreshold": 10.0, "lowerThreshold": 5.0, "buyPrice": 2800.0, "upperAlertPrice": 3080.0, "lowerAlertPrice": 2660.0, "isActive": true }, "status": "SUCCESS" }
```

---

## US9 — Real-time Portfolio Monitor

**Goal:** Authenticated user sees live portfolio gain/loss per stock and overall, with threshold breach indicators powered by Kafka.

**Actor:** Authenticated User

**Flow:**
1. Scheduler fetches live prices from Alpha Vantage every 60s
2. Prices published to Kafka `stock-prices` topic
3. Kafka consumer updates in-memory `StockPriceCache`
4. Monitor endpoint reads from cache
5. Stream API computes per-stock and overall gain/loss
6. Threshold breach checked against US8 alert settings

**Key Concepts:**
- Apache Kafka for real-time price streaming
- `ConcurrentHashMap` cache for thread-safe price storage
- Stream API for valuation computation
- `@Scheduled` for periodic price fetching
- Threshold breach detection (UPPER / LOWER / NORMAL)

**Kafka Flow:**
```
AlphaVantage API → StockPriceScheduler → KafkaProducer
                                              ↓
                                     stock-prices topic
                                              ↓
                                     KafkaConsumer → StockPriceCache
                                                           ↓
                                                   MonitorServiceImpl
```

**APIs:**
```
GET /api/v1/monitor              → Full portfolio monitor
GET /api/v1/monitor/{symbol}     → Single stock monitor
Authorization: Bearer <token>
```

**GET /api/v1/monitor Response (200):**
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
        "companyName": "Tata Consultancy Services",
        "quantity": 5,
        "buyPrice": 3900.0,
        "currentPrice": 3950.0,
        "totalInvested": 19500.0,
        "currentValue": 19750.0,
        "gainLoss": 250.0,
        "gainLossPercent": 1.28,
        "upperThreshold": 8.0,
        "lowerThreshold": 4.0,
        "upperAlertPrice": 4212.0,
        "lowerAlertPrice": 3744.0,
        "upperBreached": false,
        "lowerBreached": false,
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

**GET /api/v1/monitor/RELIANCE Response:**
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
    "alertStatus": "UPPER_BREACHED"
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

---

## ▶️ How to Run

```bash
# 1. Start Kafka (Docker)
docker-compose up -d zookeeper kafka

# Or manual Kafka start
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties

# 2. Create Kafka topic
bin/kafka-topics.sh --create --topic stock-prices --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# 3. Create MySQL database
mysql -u root -p
CREATE DATABASE investor_db;

# 4. Update application.properties
spring.datasource.password=your_password
stock.api.key=YOUR_ALPHA_VANTAGE_KEY

# 5. Run application
mvn spring-boot:run

# 6. Test
mvn test

# 7. Coverage
mvn verify
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
├── feature/US8-alert-threshold
└── feature/US9-realtime-monitor
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
20. GET    /api/v1/monitor                       → Monitor portfolio
21. GET    /api/v1/monitor/RELIANCE              → Monitor one stock
```
