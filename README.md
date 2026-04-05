# Finance Data Processing and Access Control Backend

A microservices-based backend system for managing user authentication, financial records tracking, and analytical dashboards with role-based access control and JWT authentication.

---

## 🏗️ Architecture Overview

```
                    Internet
                        │
                        ▼
                [ API Gateway ]          :4000
                JWT Validation
                Route Management
                        │
          ┌─────────────┴─────────────┐
          ▼                           ▼
  [ Auth Service ]            [ Finance Service ]
     :4001                        :4002
  Login/Register               Financial Records
  User Management              Dashboard Analytics
  Role/Permission              Summary Reports
  JWT Generation               Trends & Insights
          │                           │
          ▼                           ▼
   [ Auth DB ]                 [ Finance DB ]
   PostgreSQL                   PostgreSQL
```

---

## 🛠️ Tech Stack

| Layer              | Technology                    |
|--------------------|-------------------------------|
| Language           | Java 17                       |
| Framework          | Spring Boot 3.5.13            |
| Cloud Framework    | Spring Cloud 2025.0.0         |
| Security           | Spring Security + JWT         |
| Gateway            | Spring Cloud Gateway (WebFlux)|
| Database           | PostgreSQL                    |
| ORM                | Spring Data JPA / Hibernate   |
| Build Tool         | Maven                         |
| JWT Library        | jjwt (0.12.3)                 |

---

## 📦 Services

### 1. API Gateway (Port 4000)
- **Role**: Single entry point for all client requests
- **Features**:
  - JWT token validation via `JwtValidationFilterFactory`
  - Route management to downstream services
  - Request forwarding with user context headers:
    - `X-User-Id`
    - `X-User-Email`
    - `X-User-Roles`
  - Error handling for unauthorized requests

### 2. Auth Service (Port 4001)
- **Role**: Authentication and authorization management
- **Features**:
  - User registration and login
  - JWT access token generation
  - Refresh token management with rotation
  - Token validation endpoint for gateway
  - User activation/deactivation
  - Role and permission assignment
  - Password encryption using BCrypt (strength: 12)

### 3. Finance Service (Port 4002)
- **Role**: Financial record management and analytics
- **Features**:
  - Financial record CRUD operations
  - Dashboard summary and analytics
  - Trend analysis
  - Category-wise totals
  - Recent activity tracking
  - Advanced filtering and sorting

---

## 👥 Roles and Permissions

The system implements three role-based permission levels:

| Feature                    | VIEWER | ANALYST | ADMIN |
|----------------------------|--------|---------|-------|
| View dashboard summary     | ✅     | ✅      | ✅    |
| View category totals       | ✅     | ✅      | ✅    |
| View recent activity       | ✅     | ✅      | ✅    |
| View trends                | ❌     | ✅      | ✅    |
| View analytics             | ❌     | ✅      | ✅    |
| View records               | ❌     | ✅      | ✅    |
| Filter records             | ❌     | ✅      | ✅    |
| Create records             | ❌     | ❌      | ✅    |
| Update records             | ❌     | ❌      | ✅    |
| Delete records             | ❌     | ❌      | ✅    |
| Manage users               | ❌     | ❌      | ✅    |
| Assign roles               | ❌     | ❌      | ✅    |

---

## 🔐 Authentication Flow

```
Register  →  POST /api/v1/auth/register
             PUBLIC — no token required
             returns user info (no token)

Login     →  POST /api/v1/auth/login
             PUBLIC — no token required
             returns accessToken + refreshToken

Request   →  Authorization: Bearer <accessToken>
             gateway validates and forwards with headers

Refresh   →  POST /api/v1/auth/refresh-token
             returns new accessToken with rotated refreshToken

Logout    →  POST /api/v1/auth/logout
             revokes refresh token
```

**JWT Configuration:**
- **Access Token Expiration**: 900,000 ms (15 minutes)
- **Refresh Token Expiration**: 604,800,000 ms (7 days)
- **Algorithm**: HS256 (HMAC with SHA-256)

---

## 🚀 Getting Started

### Prerequisites
- **Java**: 17 or higher
- **Maven**: 3.8 or higher
- **PostgreSQL**: 12 or higher
- **Git** (for cloning the repository)

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/zorvyn-assessment.git
cd zorvyn-assessment
```

### 2. Create PostgreSQL Databases

```sql
CREATE DATABASE "auth-db-coach";
CREATE DATABASE "finance-db";
```

### 3. Configure Environment Variables

**Auth Service** - `auth-service/src/main/resources/application.properties`

```properties
spring.application.name=auth-service
server.port=4001

# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/auth-db-coach
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret=your-secret-key-minimum-32-characters-long
jwt.expiration=900000
jwt.refresh.token=604800000
```

**Finance Service** - `finance-service/src/main/resources/application.properties`

```properties
spring.application.name=finance-service
server.port=4002

# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/finance-db
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**API Gateway** - `api-gateway/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: auth-service-route
          uri: http://localhost:4001
          predicates:
            - Path=/api/v1/auth/**

        - id: user-service-route
          uri: http://localhost:4001
          predicates:
            - Path=/api/v1/users/**,/api/v1/roles/**,/api/v1/permissions/**
          filters:
            - name: JwtValidationFilterFactory

        - id: finance-service-route
          uri: http://localhost:4002
          predicates:
            - Path=/api/v1/records/**,/api/v1/dashboard/**
          filters:
            - name: JwtValidationFilterFactory

server:
  port: 4000

jwt:
  secret: your-secret-key-minimum-32-characters-long
```

> ⚠️ **Important**: Never commit real secrets to version control.
> Use environment variables or a secrets manager in production.

### 4. Run the Services

Open three separate terminal windows and run:

**Terminal 1 - Auth Service**
```bash
cd auth-service
mvn spring-boot:run
```

**Terminal 2 - Finance Service**
```bash
cd finance-service
mvn spring-boot:run
```

**Terminal 3 - API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```

The services will start on:
- Auth Service: `http://localhost:4001`
- Finance Service: `http://localhost:4002`
- API Gateway: `http://localhost:4000`

---

## 📡 API Endpoints

### Authentication Endpoints

| Method | Endpoint                      | Access   | Description              |
|--------|-------------------------------|----------|--------------------------|
| POST   | /api/v1/auth/register         | Public   | Register new user        |
| POST   | /api/v1/auth/login            | Public   | Login and get JWT tokens |
| POST   | /api/v1/auth/refresh-token    | Public   | Refresh access token     |
| POST   | /api/v1/auth/logout           | Auth     | Logout and revoke token  |
| GET    | /api/v1/auth/validate         | Internal | Token validation         |

### User Management Endpoints

| Method | Endpoint                      | Access  | Description              |
|--------|-------------------------------|---------|--------------------------|
| GET    | /api/v1/users                 | ADMIN   | Get all users            |
| GET    | /api/v1/users/{id}            | ADMIN   | Get specific user        |
| PATCH  | /api/v1/users/{id}/activate   | ADMIN   | Activate user            |
| PATCH  | /api/v1/users/{id}/deactivate | ADMIN   | Deactivate user          |
| POST   | /api/v1/users/{id}/roles      | ADMIN   | Assign roles to user     |
| GET    | /api/v1/users/{id}/roles      | ADMIN   | Get user's roles         |

### Financial Record Endpoints

| Method | Endpoint                      | Access              | Description               |
|--------|-------------------------------|---------------------|---------------------------|
| POST   | /api/v1/records               | ADMIN               | Create record             |
| GET    | /api/v1/records               | ANALYST, ADMIN      | Get all records (paginated)|
| GET    | /api/v1/records/{id}          | ANALYST, ADMIN      | Get single record         |
| GET    | /api/v1/records/my-records    | ANALYST, ADMIN      | Get my records            |
| GET    | /api/v1/records/filter        | ANALYST, ADMIN      | Filter records            |
| PUT    | /api/v1/records/{id}          | ADMIN               | Update record             |
| DELETE | /api/v1/records/{id}          | ADMIN               | Soft delete record        |

### Dashboard Endpoints

| Method | Endpoint                          | Access                    | Description              |
|--------|-----------------------------------|---------------------------|--------------------------|
| GET    | /api/v1/dashboard/summary         | VIEWER, ANALYST, ADMIN    | Summary totals           |
| GET    | /api/v1/dashboard/category-totals | VIEWER, ANALYST, ADMIN    | Category breakdown       |
| GET    | /api/v1/dashboard/recent          | VIEWER, ANALYST, ADMIN    | Recent activity          |
| GET    | /api/v1/dashboard/trends          | ANALYST, ADMIN            | Monthly/weekly trends    |
| GET    | /api/v1/dashboard/analytics       | ANALYST, ADMIN            | Advanced analytics       |

---

## 📊 Sample API Requests

### Register a New User

```bash
curl -X POST http://localhost:4000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "securePassword123"
  }'
```

**Response:**
```json
{
  "message": "Registration successful.",
  "userData": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "John Doe",
    "email": "john@example.com",
    "status": "ACTIVE"
  }
}
```

### Login

```bash
curl -X POST http://localhost:4000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "securePassword123"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Assign Roles to User (Admin Only)

```bash
curl -X POST http://localhost:4000/api/v1/users/{userId}/roles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-token>" \
  -d '{
    "roleIds": [
      "role-id-1",
      "role-id-2"
    ]
  }'
```

### Create Financial Record (Admin Only)

```bash
curl -X POST http://localhost:4000/api/v1/records \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-token>" \
  -d '{
    "amount": 5000.00,
    "type": "INCOME",
    "category": "SALARY",
    "date": "2024-01-15",
    "notes": "Monthly salary"
  }'
```

**Response:**
```json
{
  "id": "650e8400-e29b-41d4-a716-446655440001",
  "amount": 5000.00,
  "type": "INCOME",
  "category": "SALARY",
  "date": "2024-01-15",
  "notes": "Monthly salary",
  "createdByEmail": "john@example.com",
  "createdAt": "2024-01-15T10:30:00"
}
```

### Get Financial Records (Analyst or Admin)

```bash
curl -X GET "http://localhost:4000/api/v1/records?pageNumber=1&pageSize=10&sortByField=date&sortDirection=DESC" \
  -H "Authorization: Bearer <token>"
```

### Get Dashboard Summary (All Authenticated Users)

```bash
curl -X GET http://localhost:4000/api/v1/dashboard/summary \
  -H "Authorization: Bearer <token>"
```

**Response:**
```json
{
  "totalIncome": 10000.00,
  "totalExpense": 6500.00,
  "netBalance": 3500.00,
  "totalTransactions": 25,
  "averageTransactionAmount": 620.00,
  "topSpendingCategory": "RENT",
  "topIncomeCategory": "SALARY"
}
```

### Get Dashboard Analytics (Analyst or Admin)

```bash
curl -X GET http://localhost:4000/api/v1/dashboard/analytics \
  -H "Authorization: Bearer <analyst-token>"
```

**Response:**
```json
{
  "savingsRate": 35.0,
  "expenseToIncomeRatio": 65.0,
  "financialHealthStatus": "HEALTHY",
  "monthOverMonthIncomeGrowth": 12.5,
  "monthOverMonthExpenseGrowth": -5.2,
  "expenseBreakdown": {
    "RENT": 40.0,
    "FOOD": 30.0,
    "TRANSPORT": 15.0,
    "ENTERTAINMENT": 15.0
  }
}
```

### Refresh Token

```bash
curl -X POST http://localhost:4000/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<refresh-token>"
  }'
```

### Logout

```bash
curl -X POST "http://localhost:4000/api/v1/auth/logout?refreshToken=<refresh-token>" \
  -H "Authorization: Bearer <token>"
```

---

## 🗄️ Database Schema

### Auth Database (`auth-db-coach`)

**users** table
```
id (UUID) - Primary Key
name (VARCHAR) - User's full name
email (VARCHAR UNIQUE) - User's email
password (VARCHAR) - Bcrypt encrypted password
status (ENUM) - ACTIVE, INACTIVE, SUSPENDED
created_at (TIMESTAMP) - Account creation time
```

**roles** table
```
id (UUID) - Primary Key
name (VARCHAR UNIQUE) - Role name (VIEWER, ANALYST, ADMIN)
```

**permissions** table
```
id (UUID) - Primary Key
name (VARCHAR UNIQUE) - Permission name
```

**user_roles** table
```
user_id (UUID) - Foreign Key → users
role_id (UUID) - Foreign Key → roles
```

**role_permissions** table
```
role_id (UUID) - Foreign Key → roles
permission_id (UUID) - Foreign Key → permissions
```

**refresh_token** table
```
id (UUID) - Primary Key
token (TEXT) - Refresh token value
user_id (UUID) - Foreign Key → users
revoked (BOOLEAN) - Whether token has been used/revoked
expires_at (TIMESTAMP) - Token expiration time
created_at (TIMESTAMP) - Token creation time
```

### Finance Database (`finance-db`)

**finance_record** table
```
id (UUID) - Primary Key
amount (DOUBLE) - Transaction amount
type (ENUM) - INCOME or EXPENSE
category (ENUM) - Transaction category
date (DATE) - Transaction date (when money moved)
notes (TEXT) - Additional notes
created_by_email (VARCHAR) - Email of creator
updated_by_email (VARCHAR) - Email of last modifier
created_at (TIMESTAMP) - Record creation time
updated_at (TIMESTAMP) - Last update time
deleted (BOOLEAN) - Soft delete flag (default: false)
```

---

## 💡 Design Decisions

### 1. Soft Delete for Financial Records
Records are marked as `deleted = true` rather than permanently removed. This preserves the audit trail and allows potential restoration, which is crucial for financial compliance and data integrity.

### 2. Separate Databases
Auth and Finance services have independent databases. This enables loose coupling, independent scaling, and separate backup and disaster recovery strategies.

### 3. JWT Validation at Gateway
Token validation occurs only at the API Gateway. Downstream services trust headers forwarded by the gateway, reducing duplicate JWT logic across services and improving internal communication performance.

### 4. Refresh Token Rotation
Refresh tokens are rotated on every use. This provides enhanced security against token theft and prevents unlimited token reuse.

### 5. Role-Based Access Control
```
VIEWER   → dashboard access only (read-only)
ANALYST  → VIEWER + record view/filter + insights
ADMIN    → ANALYST + full CRUD + user management
```

### 6. Header-Based Authentication
Finance Service receives user context via headers from Gateway:
- `X-User-Id`: User's unique identifier
- `X-User-Email`: User's email address
- `X-User-Roles`: Comma-separated role list

This eliminates the need for repeated JWT validation in downstream services.

### 7. Audit Trail
Every financial record maintains:
- `createdByEmail`: Who created the record
- `createdAt`: Creation timestamp
- `updatedByEmail`: Who last modified the record
- `updatedAt`: Last modification timestamp

---

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

---

## 📚 Project Structure

```
zorvyn-assessment/
│
├── api-gateway/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pm/apigateway/
│   │   │   │   ├── ApiGatewayApplication.java
│   │   │   │   ├── filter/
│   │   │   │   │   └── JwtValidationFilterFactory.java
│   │   │   │   └── exception/handler/
│   │   │   │       └── JwtValidationExceptionHandler.java
│   │   │   └── resources/
│   │   │       └── application.yml
│   └── pom.xml
│
├── auth-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pm/authservice/
│   │   │   │   ├── AuthServiceApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── JwtService.java
│   │   │   │   │   ├── ApplicationConfig.java
│   │   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   │   ├── controller/
│   │   │   │   │   ├── AuthenticationController.java
│   │   │   │   │   └── UserController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── AuthenticationService.java
│   │   │   │   │   ├── UserService.java
│   │   │   │   │   └── RefreshTokenService.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Role.java
│   │   │   │   │   ├── Permission.java
│   │   │   │   │   └── RefreshToken.java
│   │   │   │   ├── repository/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   └── exception/
│   │   │   └── resources/
│   │   │       └── application.properties
│   └── pom.xml
│
├── finance-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pm/financeservice/
│   │   │   │   ├── FinanceServiceApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   ├── controller/
│   │   │   │   │   ├── FinanceRecordController.java
│   │   │   │   │   └── DashboardController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── FinanceRecordService.java
│   │   │   │   │   └── DashboardService.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── FinanceRecord.java
│   │   │   │   │   └── enums/
│   │   │   │   │       ├── TransactionType.java
│   │   │   │   │       └── Category.java
│   │   │   │   ├── repository/
│   │   │   │   ├── filter/
│   │   │   │   │   └── HeaderAuthFilter.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   └── exception/
│   │   │   └── resources/
│   │   │       └── application.properties
│   └── pom.xml
│
└── README.md
```

---

## 🐛 Troubleshooting

### Port Already in Use
If ports 4000, 4001, or 4002 are already in use, update the port numbers in:
- `api-gateway/src/main/resources/application.yml`
- `auth-service/src/main/resources/application.properties`
- `finance-service/src/main/resources/application.properties`

### Database Connection Errors
Ensure PostgreSQL is running:
```bash
# Windows
net start PostgreSQL-x64-XX

# macOS
brew services start postgresql

# Linux
sudo systemctl start postgresql
```

Verify databases exist:
```sql
SELECT datname FROM pg_database WHERE datname IN ('auth-db-coach', 'finance-db');
```

### JWT Token Invalid or Expired
- Check that the `jwt.secret` values match across services
- Access tokens expire in 15 minutes by default — use the refresh token to get a new one
- Ensure the Authorization header format is: `Bearer <token>`

### Hibernate DDL Issues
If you encounter schema generation issues:
1. Temporarily set `spring.jpa.hibernate.ddl-auto=create-drop` to recreate the schema
2. Once verified, revert to `update`

---

## 🔮 Future Improvements

- [ ] Service Discovery using Eureka Server
- [ ] Centralized configuration using Spring Cloud Config Server
- [ ] RS256 asymmetric JWT signing for stronger security
- [ ] Kafka for asynchronous inter-service communication
- [ ] Redis for distributed refresh token blacklist caching
- [ ] Docker and Docker Compose setup
- [ ] Kubernetes deployment manifests (Helm charts)
- [ ] Comprehensive unit and integration test suite
- [ ] Cursor-based pagination for large datasets
- [ ] Rate limiting at gateway level
- [ ] OAuth2 / OpenID Connect support
- [ ] Distributed tracing with Sleuth and Zipkin
- [ ] Health checks and readiness probes

---

## ✨ Changelog

### Version 1.0.0 (Initial Release)
- API Gateway with JWT validation
- Auth Service with user management and JWT tokens
- Finance Service with record management and analytics
- Role-based access control (VIEWER, ANALYST, ADMIN)
- Dashboard with summary, trends, and analytics
- PostgreSQL database integration

---

**Last Updated**: April 6, 2026

Happy coding! 🚀
