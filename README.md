# 📈 Portfolio Alerts App — US9: Real-time Portfolio Monitor

> Branch: `feature/US9-realtime-monitor`

---

## 🎯 Goal
Authenticated user sees live portfolio gain/loss per stock and overall, with threshold breach indicators powered by Apache Kafka.

## 👤 Actor
Authenticated User

## 📋 Flow
1. `StockPriceScheduler` fetches live prices from Alpha Vantage every 60s
2. Prices published to Kafka `stock-prices` topic
3. `StockPriceConsumer` listens and updates `StockPriceCache`
4. Monitor endpoint reads live prices from cache
5. Stream API computes per-stock and overall gain/loss
6. Threshold breach checked against US8 alert settings
7. Returns breach status: UPPER_BREACHED / LOWER_BREACHED / NORMAL

---

## 🔧 Concepts Used
| Concept | Implementation |
|---|---|
| Apache Kafka | Producer → `stock-prices` topic → Consumer |
| ConcurrentHashMap | `StockPriceCache.java` — thread-safe price store |
| Stream API | `mapToDouble().sum()` — valuation + breach count |
| `@Scheduled` | `StockPriceScheduler` — every 60s price fetch |
| `@Transactional(readOnly = true)` | Monitor read operations |
| Logger | SLF4J in all classes |

---

## 📁 Files in This Branch

```
src/main/java/com/ch/
├── config/
│   └── KafkaConfig.java              ← Producer + Consumer beans
├── controller/
│   └── MonitorController.java
├── dto/
│   ├── MonitorStockDto.java
│   └── MonitorPortfolioDto.java
├── kafka/
│   ├── StockPriceMessage.java        ← Kafka message model
│   ├── StockPriceCache.java          ← ConcurrentHashMap store
│   ├── StockPriceProducer.java       ← Publish to topic
│   ├── StockPriceConsumer.java       ← Listen + update cache
│   └── StockPriceScheduler.java      ← @Scheduled fetch
├── service/
│   └── MonitorService.java
└── serviceImpl/
    └── MonitorServiceImpl.java       ← Stream API + breach check
```

---

## 🔄 Kafka Flow

```
Alpha Vantage API
      ↓
StockPriceScheduler (@Scheduled 60s)
      ↓
StockPriceProducer
      ↓
Kafka Topic: stock-prices
      ↓
StockPriceConsumer
      ↓
StockPriceCache (ConcurrentHashMap)
      ↓
MonitorServiceImpl (reads cache)
      ↓
Stream API → MonitorPortfolioDto
```

---

## 🌐 API Endpoints

```
GET /api/v1/monitor              → Full portfolio monitor
GET /api/v1/monitor/{symbol}     → Single stock monitor
Authorization: Bearer <token>
```

### GET /api/v1/monitor Response (200)
```json
{
  "msg": "Portfolio monitoring data fetched successfully.",
  "data": {
    "stocks": [
      {
        "stockSymbol": "RELIANCE",
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
      }
    ],
    "totalInvested": 28000.0,
    "totalCurrentValue": 31000.0,
    "totalGainLoss": 3000.0,
    "totalGainLossPercent": 10.71,
    "totalStocks": 1,
    "upperBreachedCount": 1,
    "lowerBreachedCount": 0,
    "normalCount": 0,
    "lastUpdated": "2025-04-09T10:30:00"
  },
  "status": "SUCCESS",
  "statusCode": 200
}
```

### GET /api/v1/monitor/RELIANCE Response (200)
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

### Alert Status Values
| Status | Meaning |
|---|---|
| `UPPER_BREACHED` | Current price ≥ upperAlertPrice |
| `LOWER_BREACHED` | Current price ≤ lowerAlertPrice |
| `NORMAL` | Within threshold range |
| `NO_ALERT_SET` | No US8 alert configured |

---

## ⚙️ application.properties
```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=portfolio-alerts-group
stock.api.key=YOUR_API_KEY_HERE
```

## pom.xml
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

## Main class
```java
@SpringBootApplication
@EnableScheduling
public class PortfolioAlertsApplication { ... }
```

---

## 🚀 Start Kafka

```bash
docker run -d --name kafka -p 9092:9092 apache/kafka:latest

docker exec kafka kafka-topics.sh --create \
  --topic stock-prices \
  --bootstrap-server localhost:9092 \
  --partitions 1 --replication-factor 1
```

---

## 🌿 Git Commands

```bash
git checkout -b feature/US9-realtime-monitor
git add .
git commit -m "feat(US9): implement real-time portfolio monitoring with Kafka

- Kafka producer/consumer for stock-prices topic
- StockPriceCache (ConcurrentHashMap) thread-safe storage
- @Scheduled every 60s price fetch from Alpha Vantage
- GET /api/v1/monitor - full portfolio with breach status
- GET /api/v1/monitor/{symbol} - single stock monitor
- Stream API for gain/loss and overall totals

Closes #US9"
git push origin feature/US9-realtime-monitor
```
