# 📈 Portfolio Alerts App — US1: User Registration

> Branch: `feature/US1-user-registration`

---

## 🎯 Goal
Allow a new user to register securely in the system and persist their credentials in the database with proper validation and encryption.

## 👤 Actor
End User (Investor)

## 📋 Flow
1. User enters registration details (Name, Email, Password)
2. Password validated — min 8 chars, uppercase, lowercase, digit, special char
3. Backend validates email syntax and password rules via Java 8 Predicate
4. Password encrypted using BCrypt (strength 12)
5. User entity stored in MySQL via JPA
6. Suitable response returned to user

---

## 🔧 Concepts Used
| Concept | Implementation |
|---|---|
| Java 8 Predicate | `UserInputValidator.java` — validates name, email, password |
| BCrypt Encryption | `SecurityConfig.java` → `BCryptPasswordEncoder(12)` |
| JPA Persistence | `UserEntity.java` + `UserRepository.java` |
| PostgreSQL / MySQL | `application.properties` — datasource config |
| Logger | SLF4J logger in all classes |

---

## 📁 Files in This Branch

```
src/main/java/com/ch/
├── config/
│   └── SecurityConfig.java              ← BCrypt bean
├── controller/
│   └── UserRegistrationController.java  ← POST /api/v1/auth/register
├── customexception/
│   └── UserRegistrationException.java
├── dto/
│   ├── CommonDto.java
│   └── UserRegisterDto.java
├── entity/
│   └── UserEntity.java
├── repository/
│   └── UserRepository.java
├── service/
│   └── UserRegistrationService.java
├── serviceImpl/
│   └── UserRegistrationServiceImpl.java
└── utils/
    └── UserInputValidator.java          ← Java 8 Predicate validation

src/test/java/com/ch/
├── utils/
│   └── UserInputValidatorTest.java
├── serviceImpl/
│   └── UserRegistrationServiceImplTest.java
└── controller/
    └── UserRegistrationControllerTest.java

src/main/resources/
└── application.properties
```

---

## 🌐 API Endpoint

```
POST /api/v1/auth/register
Content-Type: application/json
```

### ✅ Success Request
```json
{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "Secret@123"
}
```

### ✅ Success Response (201)
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

### ❌ Invalid Email (400)
```json
{
  "msg": "Invalid email format.",
  "data": null,
  "status": "FAILED",
  "statusCode": 400
}
```

### ❌ Weak Password (400)
```json
{
  "msg": "Password must contain at least one uppercase letter.",
  "data": null,
  "status": "FAILED",
  "statusCode": 400
}
```

### ❌ Duplicate Email (400)
```json
{
  "msg": "Email 'john@example.com' is already registered.",
  "data": null,
  "status": "FAILED",
  "statusCode": 400
}
```

### ❌ Duplicate Username (400)
```json
{
  "msg": "Username 'johndoe' is already taken.",
  "data": null,
  "status": "FAILED",
  "statusCode": 400
}
```

---

## 🔐 Password Rules
| Rule | Example |
|---|---|
| Minimum 8 characters | `Secret@1` ✅ |
| At least 1 uppercase | `S` ✅ |
| At least 1 lowercase | `ecret` ✅ |
| At least 1 digit | `1` ✅ |
| At least 1 special char `@#$%^*-_` | `@` ✅ |

---

## 🧪 Unit Tests
| Test Class | Tests | What is Tested |
|---|---|---|
| `UserInputValidatorTest` | 20 | Predicate — name, email, password rules |
| `UserRegistrationServiceImplTest` | 7 | Service — success, validation, duplicate, encryption |
| `UserRegistrationControllerTest` | 3 | API — MockMvc request/response |

```bash
# Run tests
mvn test

# Coverage report
mvn verify
# Report: target/site/jacoco/index.html
```

---

## ▶️ How to Run

```bash
# 1. Create MySQL database
CREATE DATABASE investor_db;

# 2. Update application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/investor_db
spring.datasource.username=root
spring.datasource.password=your_password

# 3. Run
mvn spring-boot:run

# 4. Test with Postman
POST http://localhost:8080/api/v1/auth/register
```

---

## 🌿 Git Commands

```bash
git checkout -b feature/US1-user-registration
git add .
git commit -m "feat(US1): implement user registration with BCrypt, JPA, Predicate validation

- POST /api/v1/auth/register endpoint
- Java 8 Predicate validation for email and password rules
- BCrypt (strength 12) password encryption
- JPA User entity persisted to MySQL
- Duplicate username/email detection
- SLF4J logger for all user actions
- Unit tests: UserInputValidatorTest, UserRegistrationServiceImplTest
- JaCoCo code coverage configured (min 80%)

Closes #US1"
git push origin feature/US1-user-registration
```
