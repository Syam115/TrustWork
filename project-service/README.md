# Project Service

The Project Service manages all project-related operations in the TrustWork platform, including project creation, bidding, and project lifecycle management. It handles the core business logic for connecting clients with freelancers.

## Features

- Project creation and management
- Bid placement and management
- Project status tracking
- Role-based access control
- JWT token validation
- Integration with Auth Service for user verification

## Technology Stack

- **Spring Boot 3.2.5**
- **Spring Security with OAuth2 Resource Server**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT Validation**
- **Lombok**
- **Validation**
- **OpenFeign** (for service communication)

## Configuration

The service runs on port **8082** and connects to PostgreSQL database `trustwork_project`.

Key configurations in `application.yaml`:
- Database: `jdbc:postgresql://localhost:5432/trustwork_project`
- JWT Secret: For token validation (matches Auth Service)
- Eureka Client: Registers with discovery server at `http://localhost:8761/eureka/`

## API Endpoints

Base URL: `http://localhost:8082` (or through API Gateway: `http://localhost:8080`)

All endpoints require JWT authentication in the Authorization header: `Bearer {jwt-token}`

### Project Endpoints

#### Create Project
```http
POST /api/projects
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "title": "Website Development",
  "description": "Build a responsive website",
  "budget": 5000.00,
  "deadline": "2024-12-31"
}
```

**Response:**
```json
{
  "id": "uuid",
  "title": "Website Development",
  "description": "Build a responsive website",
  "budget": 5000.00,
  "deadline": "2024-12-31T00:00:00Z",
  "status": "OPEN",
  "clientId": "client-uuid",
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

#### Get All Open Projects
```http
GET /api/projects
```

**Response:** Array of open projects

#### Get Project by ID
```http
GET /api/projects/{id}
```

#### Get My Projects (Client Only)
```http
GET /api/projects/my-projects
Authorization: Bearer {jwt-token}
```

### Bid Endpoints

#### Place Bid
```http
POST /api/projects/{projectId}/bids
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "amount": 4500.00,
  "proposal": "I can deliver this project within 2 weeks",
  "estimatedDays": 14
}
```

**Response:**
```json
{
  "id": "uuid",
  "projectId": "project-uuid",
  "freelancerId": "freelancer-uuid",
  "amount": 4500.00,
  "proposal": "I can deliver this project within 2 weeks",
  "estimatedDays": 14,
  "status": "PENDING",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

#### Get Bids for Project (Project Owner Only)
```http
GET /api/projects/{projectId}/bids
Authorization: Bearer {jwt-token}
```

#### Accept Bid
```http
PATCH /api/projects/{projectId}/bids/{bidId}/accept
Authorization: Bearer {jwt-token}
```

## Data Models

### Project Entity
- `id`: UUID (Primary Key)
- `title`: String (Required)
- `description`: String (Required)
- `budget`: BigDecimal (Required)
- `deadline`: LocalDate (Required)
- `status`: Enum (OPEN, IN_PROGRESS, COMPLETED, CANCELLED)
- `clientId`: UUID (Foreign Key to User)
- `freelancerId`: UUID (Foreign Key to User, nullable)
- `createdAt`: Timestamp
- `updatedAt`: Timestamp

### Bid Entity
- `id`: UUID (Primary Key)
- `projectId`: UUID (Foreign Key to Project)
- `freelancerId`: UUID (Foreign Key to User)
- `amount`: BigDecimal (Required)
- `proposal`: String (Required)
- `estimatedDays`: Integer (Required)
- `status`: Enum (PENDING, ACCEPTED, REJECTED)
- `createdAt`: Timestamp

### Enums

#### ProjectStatus
- `OPEN`: Project is open for bids
- `IN_PROGRESS`: Project is assigned and in progress
- `COMPLETED`: Project is completed
- `CANCELLED`: Project is cancelled

#### BidStatus
- `PENDING`: Bid is waiting for client decision
- `ACCEPTED`: Bid is accepted by client
- `REJECTED`: Bid is rejected by client

## Security

- JWT tokens are validated using OAuth2 Resource Server
- User roles determine access permissions:
  - **CLIENT**: Can create projects, view their projects, see bids, accept bids
  - **FREELANCER**: Can view open projects, place bids
- User ID is extracted from JWT token for authorization

## Running the Service

### Prerequisites
- PostgreSQL database `trustwork_project`
- Discovery Server running on port 8761
- Auth Service running (for JWT validation)

### Start Command
```bash
cd project-service
mvn spring-boot:run
```

### Build JAR
```bash
mvn clean package
java -jar target/project-service-1.0-SNAPSHOT.jar
```

## Dependencies

Key dependencies include:
- `spring-boot-starter-data-jpa`: Database operations
- `spring-boot-starter-security`: Security configuration
- `spring-boot-starter-oauth2-resource-server`: JWT validation
- `spring-boot-starter-web`: REST API
- `spring-cloud-starter-netflix-eureka-client`: Service discovery
- `spring-cloud-starter-openfeign`: Service communication
- `jjwt`: JWT handling
- `postgresql`: Database driver
- `lombok`: Code generation
- `spring-boot-starter-validation`: Input validation

## Development

### Project Structure
```
project-service/
├── src/main/java/io/eikon/projectservice/
│   ├── ProjectServiceApplication.java
│   ├── config/
│   ├── controller/
│   │   ├── ProjectController.java
│   │   └── BidController.java
│   ├── dto/
│   ├── entity/
│   │   ├── Project.java
│   │   ├── Bid.java
│   │   ├── ProjectStatus.java
│   │   └── BidStatus.java
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

## Business Logic

### Project Creation
- Only users with CLIENT role can create projects
- Projects are created with OPEN status
- Client ID is extracted from JWT token

### Bidding
- Only users with FREELANCER role can place bids
- Bids can only be placed on OPEN projects
- Freelancers can only place one bid per project

### Bid Acceptance
- Only project owners (clients) can view and accept bids
- Accepting a bid changes project status to IN_PROGRESS
- Accepted bid status becomes ACCEPTED, others become REJECTED

## Error Handling

Custom exceptions for:
- Project not found
- Access denied (wrong user role or ownership)
- Invalid bid operations
- Authentication failures

## Integration

This service integrates with:
- **Auth Service**: For user authentication and JWT validation
- **Discovery Server**: For service registration
- **API Gateway**: Routes external requests
