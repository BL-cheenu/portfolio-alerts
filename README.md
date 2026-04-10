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
| **Message Queue** | Apache Kafka + RabbitMQ |
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
- Receive automated email alerts when stock price crosses set thresholds via RabbitMQ

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
│   │   ├── MonitorServiceImpl.java       ← US9
│   │   └── AlertHistoryServiceImpl.java  ← US10
│   └── utils/
│       ├── UserInputValidator.java
│       ├── JwtUtil.java
│       ├── ExcelParserUtil.java
│       └── NseTop50Symbols.java
│       └── JwtAuthFilter.java
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
| `alerts` | Upper/lower % threshold settings |
| `alert_history` | All email alerts sent — for UI display |

---

## 🔐 Authentication

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

## 📦 Tech Stack

| Dependency | Version | Purpose |
|---|---|---|
| Spring Boot | 4.0.5 | Framework |
| Spring Data JPA | - | ORM |
| Spring Security Crypto | - | BCrypt |
| MySQL Connector | - | DB Driver |
| JJWT | 0.11.5 | JWT |
| Apache POI | 5.2.5 | Excel parsing |
| Spring Kafka | - | Real-time prices |
| Spring AMQP (RabbitMQ) | - | Alert messaging |
| Spring Mail | - | Email alerts |
| Lombok | - | Boilerplate |
| JUnit 5 | 5.10.1 | Testing |
| Mockito | 5.8.0 | Mocking |
| JaCoCo | 0.8.11 | Coverage (80%) |

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

**Excel Format:** `Stock Symbol | Company Name | Quantity | Buy Price`

---

## US6 — Create Portfolio (UI Form)

**APIs:**
```
POST /api/v1/portfolio
GET  /api/v1/portfolio/valuation
```

**Request:**
```json
{ "stockSymbol": "RELIANCE", "companyName": "Reliance Industries", "quantity": 10, "buyPrice": 2800.00 }
```

---

## US7 — Manage Portfolio

**APIs:**
```
GET    /api/v1/portfolio
PUT    /api/v1/portfolio/{id}
DELETE /api/v1/portfolio/{id}
DELETE /api/v1/portfolio
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

**Request:**
```json
{ "stockSymbol": "RELIANCE", "upperThreshold": 10.0, "lowerThreshold": 5.0 }
```
**Response:**
```json
{ "data": { "stockSymbol": "RELIANCE", "upperThreshold": 10.0, "lowerThreshold": 5.0, "buyPrice": 2800.0, "upperAlertPrice": 3080.0, "lowerAlertPrice": 2660.0 }, "status": "SUCCESS" }
```

---

## US9 — Real-time Portfolio Monitor

**Kafka Flow:**
```
AlphaVantage → Scheduler → KafkaProducer → stock-prices topic
                                                    ↓
                                           KafkaConsumer → StockPriceCache
                                                                ↓
                                                     MonitorServiceImpl (Stream API)
```

**APIs:**
```
GET /api/v1/monitor
GET /api/v1/monitor/{symbol}
```

**Response (200):**
```json
{
  "data": {
    "stocks": [
      { "stockSymbol": "RELIANCE", "quantity": 10, "buyPrice": 2800.0, "currentPrice": 3100.0, "gainLoss": 3000.0, "gainLossPercent": 10.71, "alertStatus": "UPPER_BREACHED", "upperBreached": true, "lowerBreached": false }
    ],
    "totalInvested": 28000.0, "totalCurrentValue": 31000.0,
    "totalGainLoss": 3000.0, "totalGainLossPercent": 10.71,
    "upperBreachedCount": 1, "lowerBreachedCount": 0, "normalCount": 0,
    "lastUpdated": "2025-04-09T10:30:00"
  },
  "status": "SUCCESS"
}
```

---

## US10 — Send Alert Email via RabbitMQ

**Goal:** System sends HTML email to user when stock price crosses alert threshold.

**Actor:** System (automated)

**RabbitMQ Flow:**
```
AlertGenerator (@Scheduled 60s)
      ↓ checks Kafka price cache vs US8 thresholds
      ↓ if breached →
RabbitTemplate → alert.exchange → alert.email.queue
                                          ↓
                                  AlertConsumer
                                          ↓
                              AlertEmailService (HTML email)
                                          +
                              AlertHistoryService (save to DB)
                                          ↓
                              Dead Letter Queue (on failure)
```

**Email Content:** Stock name, buy price, current price, alert price, quantity, total invested, current value, gain/loss, breach type, timestamp.

**APIs for UI Alert History:**
```
GET /api/v1/alert-history                  → All sent alerts
GET /api/v1/alert-history/stock/{symbol}   → By stock
Authorization: Bearer <token>
```

**GET /api/v1/alert-history Response (200):**
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
    }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

---

## ▶️ How to Run

```bash
# 1. Start Kafka
docker run -d --name kafka -p 9092:9092 apache/kafka:latest

# 2. Start RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management

# 3. Create MySQL database
CREATE DATABASE investor_db;

# 4. Update application.properties
spring.datasource.password=your_password
stock.api.key=YOUR_ALPHA_VANTAGE_KEY
spring.mail.username=your_email@gmail.com
spring.mail.password=your_gmail_app_password

# 5. Run application
mvn spring-boot:run

# 6. Test
mvn test

# 7. Coverage report
mvn verify
# target/site/jacoco/index.html
```

---

## pom.xml dependencies to add for US10

```xml
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
├── feature/US9-realtime-monitor
└── feature/US10-alert-email
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
