# 📈 Portfolio Alerts App — US2: User Login & Authentication

> Branch: `feature/US2-user-login`

---

## 🎯 Goal
Allow a registered user to login with valid username and password and receive a JWT token for authenticated access.

## 👤 Actor
Registered User

## 📋 Flow
1. User submits login credentials (username + password)
2. Backend fetches user via `Optional<UserEntity>`
3. BCrypt password verification using `passwordEncoder.matches()`
4. JWT token generated after successful verification (24hr expiry, HS256)
5. Token returned in response for use in subsequent requests

---

## 🔧 Concepts Used
| Concept | Implementation |
|---|---|
| Optional | `UserRepository.findByUsername()` returns `Optional<UserEntity>` |
| JWT | `JwtUtil.java` — generate, validate, extract username |
| BCrypt verify | `passwordEncoder.matches(rawPwd, encodedPwd)` |
| Logger | SLF4J in all classes |

---

## 📁 Files in This Branch

```
src/main/java/com/ch/
├── controller/
│   └── UserLoginController.java     ← POST /api/v1/auth/login
├── customexception/
│   └── UserLoginException.java
├── dto/
│   ├── LoginRequestDto.java
│   └── LoginResponseDto.java
├── service/
│   └── UserLoginService.java
├── serviceImpl/
│   └── UserLoginServiceImpl.java
└── utils/
    └── JwtUtil.java                 ← JWT generate/validate
```

---

## 🌐 API Endpoint

```
POST /api/v1/auth/login
Content-Type: application/json
```

### ✅ Success Request
```json
{
  "username": "johndoe",
  "password": "Secret@123"
}
```

### ✅ Success Response (200)
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

### ❌ Wrong Password (401)
```json
{
  "msg": "Invalid username or password.",
  "data": null,
  "status": "FAILED",
  "statusCode": 401
}
```

### ❌ Blank Username (401)
```json
{
  "msg": "Username must not be blank.",
  "data": null,
  "status": "FAILED",
  "statusCode": 401
}
```

---

## 🔐 JWT Token Usage
After login, use the token in all subsequent API calls:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxxxx
```

Token details:
- Algorithm: HS256
- Expiry: 24 hours
- Subject: username

---

## 🌿 Git Commands

```bash
git checkout -b feature/US2-user-login
git add .
git commit -m "feat(US2): implement user login with JWT authentication

- POST /api/v1/auth/login endpoint
- Optional<UserEntity> for user fetching
- BCrypt password verification
- JWT token generation (24hr expiry, HS256)
- UserLoginException for auth failures
- 401 Unauthorized for invalid credentials

Closes #US2"
git push origin feature/US2-user-login
```
