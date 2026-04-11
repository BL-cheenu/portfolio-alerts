# 📈 Portfolio Alerts

![Java](https://img.shields.io/badge/Java-17-green)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-blue)
![Kafka](https://img.shields.io/badge/Kafka-Message%20Broker-orange)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Alerts-purple)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

A real-time stock portfolio monitoring and alert system built with Spring Boot.  
Users manage NSE stock portfolios, set price alerts, and receive email notifications — powered by Kafka and RabbitMQ.

---

## Features

- User registration and JWT-based authentication
- NSE Top-50 stock master management
- Create, update, and manage stock portfolios
- Bulk portfolio upload via Excel file
- Set price-based alerts (above/below threshold)
- Real-time stock price streaming via **Apache Kafka**
- Alert email delivery via **RabbitMQ**
- Alert history tracking

---

## Tech Stack

| Layer             | Technology                     |
|-------------------|-------------------------------|
| Backend Framework | Spring Boot 3.x               |
| Security          | Spring Security + JWT         |
| Database          | MySQL + Spring Data JPA       |
| Message Broker    | Apache Kafka, RabbitMQ        |
| Build Tool        | Maven                         |
| Testing           | JUnit 5 + Mockito             |
| File Parsing      | Apache POI (Excel)            |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.x
- Apache Kafka (running on `localhost:9092`)
- RabbitMQ (running on `localhost:5672`)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/portfolio-alerts.git
cd portfolio-alerts
```

### 2. Configure the database

Create a MySQL database and update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio_alerts
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# JWT
app.jwt.secret=your_jwt_secret_key
app.jwt.expiration=86400000
```

### 3. Start Kafka & RabbitMQ

```bash
# Kafka (from your Kafka install directory)
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties

# RabbitMQ
rabbitmq-server
```

### 4. Build and run

```bash
mvn clean install
mvn spring-boot:run
```

App runs on → `http://localhost:8080`

### 5. Run tests

```bash
mvn test
```

---
