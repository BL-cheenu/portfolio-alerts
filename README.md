# 📈 Portfolio Alerts App — US10: Send Alert Email via RabbitMQ

> Branch: `feature/US10-alert-email`

---

## 🎯 Goal
System automatically sends HTML email to user when stock price crosses the set threshold. Alert history stored in DB for UI display.

## 👤 Actor
System (automated trigger) + Authenticated User (receiver)

## 📋 Flow
1. `AlertGenerator` runs every 60s via `@Scheduled`
2. For each user, checks portfolio vs US8 thresholds
3. Gets live prices from `StockPriceCache` (Kafka updated)
4. If breached → serializes `AlertEmailMessage` as JSON String
5. Publishes to RabbitMQ `alert.exchange` → `alert.email.queue`
6. `AlertConsumer` receives message from queue
7. Sends HTML email via `AlertEmailService` (Spring Mail)
8. Saves to `alert_history` table for UI display
9. On failure → message goes to Dead Letter Queue (DLQ)

---

## 🔧 Concepts Used
| Concept | Implementation |
|---|---|
| RabbitMQ Producer | `AlertGenerator.java` → `RabbitTemplate.convertAndSend()` |
| RabbitMQ Consumer | `AlertConsumer.java` → `@RabbitListener` |
| Dead Letter Queue | Failed messages auto-routed to `alert.email.dlq` |
| Spring Mail | `AlertEmailService.java` → HTML email |
| SimpleMessageConverter | Fix for deprecated `Jackson2JsonMessageConverter` in Spring AMQP 4.0 |
| `@Scheduled` | AlertGenerator runs every 60s |
| Logger | SLF4J in all classes |

---

## 📁 Files in This Branch

```
src/main/java/com/ch/
├── config/
│   └── RabbitMQConfig.java           ← Queue, Exchange, Binding, DLQ
├── controller/
│   └── AlertHistoryController.java
├── dto/
│   └── AlertHistoryDto.java
├── entity/
│   └── AlertHistoryEntity.java       ← alert_history table
├── rabbitmq/
│   ├── AlertEmailMessage.java        ← RabbitMQ message model
│   ├── AlertGenerator.java           ← Producer (@Scheduled)
│   └── AlertConsumer.java            ← Consumer (@RabbitListener)
├── repository/
│   └── AlertHistoryRepository.java
├── service/
│   ├── AlertEmailService.java        ← HTML email sender
│   └── AlertHistoryService.java
└── serviceImpl/
    └── AlertHistoryServiceImpl.java
```

---

## 🔄 RabbitMQ Flow

```
AlertGenerator (@Scheduled 60s)
      ↓ check StockPriceCache vs thresholds
      ↓ breach detected →
JSON String (AlertEmailMessage)
      ↓
RabbitTemplate.convertAndSend()
      ↓
alert.exchange (Direct Exchange)
      ↓
alert.email.queue
      ↓
AlertConsumer (@RabbitListener)
      ↓
AlertEmailService → HTML Email sent ✉️
      +
AlertHistoryService → saved to alert_history table
      ↓ (on failure)
alert.email.dlq (Dead Letter Queue)
```

---

## 🌐 API Endpoints (Alert History for UI)

```
GET /api/v1/alert-history                  → All sent alerts
GET /api/v1/alert-history/stock/{symbol}   → By stock
Authorization: Bearer <token>
```

### GET /api/v1/alert-history Response (200)
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

### GET /api/v1/alert-history/stock/RELIANCE Response (200)
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
      "emailSentTo": "john@example.com",
      "triggeredAt": "2025-04-09T10:30:00"
    }
  ],
  "status": "SUCCESS",
  "statusCode": 200
}
```

---

## 📧 Email Content
HTML email sent to user includes:
- Breach type (🔺 Upper / 🔻 Lower)
- Stock name + symbol
- Buy price, Current price, Alert price, Threshold %
- Quantity, Total invested, Current value
- Gain/Loss amount and %
- Triggered timestamp

---

## ⚙️ application.properties
```properties
# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Email (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_gmail_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## pom.xml
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

---

## 🚀 Start RabbitMQ

```bash
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:management

# Management UI
http://localhost:15672
Username: guest | Password: guest
```

---

## 📧 Gmail App Password Setup
```
Google Account → Security
→ 2-Step Verification (enable)
→ App Passwords → Generate
→ Copy to spring.mail.password
```

---

## 🌿 Git Commands

```bash
git checkout -b feature/US10-alert-email
git add .
git commit -m "feat(US10): implement alert email via RabbitMQ

- RabbitMQ producer (AlertGenerator) - @Scheduled threshold check
- RabbitMQ consumer (AlertConsumer) - email + history save
- HTML email with stock details, gain/loss, breach info
- Dead Letter Queue for failed messages
- SimpleMessageConverter fix (Jackson2JsonMessageConverter deprecated)
- alert_history table for UI display
- GET /api/v1/alert-history
- GET /api/v1/alert-history/stock/{symbol}

Closes #US10"
git push origin feature/US10-alert-email
```
