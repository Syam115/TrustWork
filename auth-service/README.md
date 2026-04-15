# Auth Service

The Auth Service is a microservice responsible for user authentication, registration, and user management in the TrustWork platform. It handles JWT token generation, validation, and user profile operations.

## Features

- User registration and login
- JWT token-based authentication
- User profile management
- Role-based access control (CLIENT, FREELANCER, ADMIN)
- Password encryption
- Token validation

## Technology Stack

- **Spring Boot 3.2.5**
- **Spring Security**
- **JWT (JSON Web Tokens)**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **Validation**

## Configuration

The service runs on port **8081** and connects to PostgreSQL database `trustwork_auth`.

Key configurations in `application.yaml`:
- Database: `jdbc:postgresql://localhost:5432/trustwork_auth`
- JWT Secret: Configured for token signing
- JWT Expiration: 24 hours (86400000 ms)
- Eureka Client: Registers with discovery server at `http://localhost:8761/eureka/`

## API Endpoints

Base URL: `http://localhost:8081` (or through API Gateway: `http://localhost:8080`)

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "role": "CLIENT"
}
```

**Response:**
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "CLIENT",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "jwt-token-here",
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "CLIENT"
  }
}
```

#### Validate Token
```http
GET /api/auth/validate?token=jwt-token-here
```

**Response:** Username if valid, 401 if invalid

### User Management Endpoints

#### Get User by ID
```http
GET /api/users/{id}
Authorization: Bearer {jwt-token}
```

#### Get Current User Profile
```http
GET /api/users/me
Authorization: Bearer {jwt-token}
```

#### Update User Profile
```http
PUT /api/users/{id}
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "firstName": "Updated Name",
  "lastName": "Updated Last Name",
  "bio": "Updated bio"
}
```

## Data Models

### User Entity
- `id`: UUID (Primary Key)
- `email`: String (Unique, Required)
- `password`: String (Encrypted, Required)
- `firstName`: String (Required)
- `lastName`: String (Required)
- `role`: Enum (CLIENT, FREELANCER, ADMIN)
- `bio`: String (Optional)
- `isActive`: Boolean (Default: true)
- `createdAt`: Timestamp
- `updatedAt`: Timestamp

### User Roles
- **CLIENT**: Can create and manage projects
- **FREELANCER**: Can browse projects and place bids
- **ADMIN**: Administrative privileges

## Security

- Passwords are encrypted using Spring Security's password encoder
- JWT tokens are signed with HS256 algorithm
- Tokens expire after 24 hours
- Role-based authorization for protected endpoints

## Running the Service

### Prerequisites
- PostgreSQL database `trustwork_auth`
- Discovery Server running on port 8761

### Start Command
```bash
cd auth-service
mvn spring-boot:run
```

### Build JAR
```bash
mvn clean package
java -jar target/auth-service-1.0-SNAPSHOT.jar
```

## Dependencies

Key dependencies include:
- `spring-boot-starter-data-jpa`: Database operations
- `spring-boot-starter-security`: Authentication and authorization
- `spring-boot-starter-web`: REST API
- `spring-cloud-starter-netflix-eureka-client`: Service discovery
- `jjwt`: JWT token handling
- `postgresql`: Database driver
- `lombok`: Code generation
- `spring-boot-starter-validation`: Input validation

## Development

### Project Structure
```
auth-service/
├── src/main/java/io/eikon/authservice/
│   ├── AuthServiceApplication.java
│   ├── config/
│   │   ├── JwtUtil.java
│   │   └── UserPrincipal.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   └── UserController.java
│   ├── dto/
│   ├── entity/
│   │   ├── Role.java
│   │   └── User.java
│   ├── repository/
│   ├── service/
│   └── exception/
├── src/main/resources/
│   └── application.yaml
└── pom.xml
```

### Testing
```bash
mvn test
```

## Error Handling

The service includes custom exception handling for:
- User not found
- Invalid credentials
- Token validation errors
- Access denied scenarios

## Integration

This service integrates with:
- **Discovery Server**: For service registration
- **API Gateway**: Routes external requests
- **Project Service**: Provides user authentication for project operations
