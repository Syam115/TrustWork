# TrustWork

TrustWork is a microservices-based platform that connects clients with freelancers for project collaboration. Built with Spring Boot and Spring Cloud, it provides a secure and scalable solution for managing projects, bids, and user authentication.

## Architecture

The application consists of the following microservices:

- **API Gateway**: Entry point for all client requests, handles routing and load balancing
- **Discovery Server**: Service registry using Netflix Eureka for service discovery
- **Auth Service**: Handles user authentication, registration, and JWT token management
- **Project Service**: Manages projects, bids, and project lifecycle

## Technology Stack

- **Java 21**
- **Spring Boot 3.2.5**
- **Spring Cloud 2023.0.3**
- **PostgreSQL** (Database)
- **JWT** (Authentication)
- **Maven** (Build tool)
- **Netflix Eureka** (Service Discovery)
- **Spring Cloud Gateway** (API Gateway)

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+
- Docker (optional, for containerized deployment)

## Database Setup

Create the following PostgreSQL databases:

```sql
CREATE DATABASE trustwork_auth;
CREATE DATABASE trustwork_project;
```

Default connection settings (can be modified in application.yaml files):
- Host: localhost
- Port: 5432
- Username: postgres
- Password: postgres

## Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd TrustWork
   ```

2. **Build all services**
   ```bash
   mvn clean install
   ```

3. **Start services in order**:
   
   a. **Discovery Server** (Port 8761)
   ```bash
   cd discovery-server
   mvn spring-boot:run
   ```
   
   b. **Auth Service** (Port 8081)
   ```bash
   cd auth-service
   mvn spring-boot:run
   ```
   
   c. **Project Service** (Port 8082)
   ```bash
   cd project-service
   mvn spring-boot:run
   ```
   
   d. **API Gateway** (Port 8080)
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

4. **Access the application**
   - API Gateway: http://localhost:8080
   - Eureka Dashboard: http://localhost:8761

## API Endpoints

All requests should go through the API Gateway at `http://localhost:8080`.

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/validate` - Token validation
- `GET /api/users/{id}` - Get user profile
- `GET /api/users/me` - Get current user profile
- `PUT /api/users/{id}` - Update user profile

### Projects
- `POST /api/projects` - Create project (Client only)
- `GET /api/projects` - Get all open projects
- `GET /api/projects/{id}` - Get project details
- `GET /api/projects/my-projects` - Get user's projects (Client only)

### Bids
- `POST /api/projects/{projectId}/bids` - Place bid (Freelancer only)
- `GET /api/projects/{projectId}/bids` - Get bids for project (Project owner only)
- `PATCH /api/projects/{projectId}/bids/{bidId}/accept` - Accept bid (Project owner only)

## User Roles

- **CLIENT**: Can create projects and manage bids
- **FREELANCER**: Can browse projects and place bids
- **ADMIN**: Administrative access (future feature)

## Development

Each service can be developed independently. Use the following commands for individual services:

```bash
# Run with hot reload
mvn spring-boot:run

# Run tests
mvn test

# Build JAR
mvn clean package
```

## Configuration

Configuration files are located in `src/main/resources/application.yaml` for each service. Key configurations include:

- Database connections
- Eureka client settings
- JWT secret and expiration
- Server ports

## Security

- JWT-based authentication
- Role-based access control
- Password encryption
- CORS enabled for cross-origin requests

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes and add tests
4. Submit a pull request

## License

This project is licensed under the MIT License.
