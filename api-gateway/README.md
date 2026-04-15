# API Gateway

The API Gateway serves as the single entry point for all client requests in the TrustWork microservices architecture. It handles request routing, load balancing, and provides a unified API interface for external clients while hiding the complexity of the underlying microservices.

## Features

- Request routing and load balancing
- Service discovery integration
- Unified API endpoints
- Cross-origin resource sharing (CORS)
- Request filtering and transformation
- Centralized access point

## Technology Stack

- **Spring Boot 3.2.5**
- **Spring Cloud Gateway**
- **Netflix Eureka Client**
- **Reactive Web Framework**

## Configuration

The service runs on port **8080** and acts as the main entry point for the application.

Key configurations in `application.yaml`:
- Server Port: 8080
- Eureka Client: Registers with discovery server at `http://localhost:8761/eureka/`
- Gateway Routes: Defines routing rules for different services

## Gateway Routes

The API Gateway routes requests based on path patterns:

### Auth Service Routes
- **Route ID**: auth-service
- **URI**: lb://AUTH-SERVICE (Load balanced)
- **Predicates**: Path=/api/auth/**, /api/users/**
- **Description**: Routes authentication and user management requests

### Project Service Routes
- **Route ID**: project-service
- **URI**: lb://PROJECT-SERVICE (Load balanced)
- **Predicates**: Path=/api/project/**
- **Description**: Routes project and bidding related requests

## How It Works

### Request Flow
1. **Client Request**: External clients send requests to `http://localhost:8080`
2. **Route Matching**: Gateway matches request path to configured routes
3. **Service Discovery**: Resolves service names to actual instances via Eureka
4. **Load Balancing**: Distributes requests across available service instances
5. **Request Forwarding**: Forwards request to appropriate microservice
6. **Response**: Returns response from microservice to client

### Load Balancing
- Uses Ribbon for client-side load balancing
- Automatically discovers service instances
- Distributes load across healthy instances
- Handles service instance failures gracefully

## Running the Service

### Prerequisites
- Discovery Server running on port 8761
- Auth Service and Project Service registered with Eureka

### Start Command
```bash
cd api-gateway
mvn spring-boot:run
```

### Build JAR
```bash
mvn clean package
java -jar target/api-gateway-1.0-SNAPSHOT.jar
```

## Dependencies

Key dependencies include:
- `spring-cloud-starter-gateway`: Gateway functionality
- `spring-cloud-starter-netflix-eureka-client`: Service discovery

## Development

### Project Structure
```
api-gateway/
├── src/main/java/io/eikon/apigateway/
│   └── ApiGatewayApplication.java
├── src/main/resources/
│   └── application.yaml
└── pom.xml
```

### Main Application Class
```java
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

## API Endpoints

All external API calls go through the gateway at `http://localhost:8080`:

### Authentication Endpoints
- `POST /api/auth/register` → Auth Service
- `POST /api/auth/login` → Auth Service
- `GET /api/auth/validate` → Auth Service
- `GET /api/users/{id}` → Auth Service
- `GET /api/users/me` → Auth Service
- `PUT /api/users/{id}` → Auth Service

### Project Endpoints
- `POST /api/projects` → Project Service
- `GET /api/projects` → Project Service
- `GET /api/projects/{id}` → Project Service
- `GET /api/projects/my-projects` → Project Service
- `POST /api/projects/{projectId}/bids` → Project Service
- `GET /api/projects/{projectId}/bids` → Project Service
- `PATCH /api/projects/{projectId}/bids/{bidId}/accept` → Project Service

## Configuration Details

### Route Configuration
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://AUTH-SERVICE
          predicates:
            - Path=/api/auth/**, /api/users/**
        
        - id: project-service
          uri: lb://PROJECT-SERVICE
          predicates:
            - Path=/api/project/**
```

### Eureka Configuration
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}
```

## Advanced Features

### Filters
The gateway supports various filters for:
- Request/Response transformation
- Authentication and authorization
- Rate limiting
- Logging and monitoring
- CORS handling

### Circuit Breaker
Can be configured with Resilience4j for fault tolerance.

### Monitoring
- Integration with Spring Boot Actuator
- Health checks and metrics
- Request tracing with Spring Cloud Sleuth

## Security Considerations

For production deployments:
- Implement authentication at gateway level
- Add rate limiting to prevent abuse
- Configure HTTPS/TLS
- Add request validation and sanitization
- Implement API versioning strategies

## Troubleshooting

### Common Issues
- **404 Not Found**: Check route predicates match request paths
- **503 Service Unavailable**: Service instances not available in Eureka
- **Timeout**: Service instances are slow or unresponsive

### Debugging
```bash
# Check gateway logs
# Verify Eureka registration
curl http://localhost:8761/eureka/apps

# Test direct service access
curl http://localhost:8081/api/auth/health
curl http://localhost:8082/api/projects/health
```

### Route Testing
```bash
# Test auth routes
curl http://localhost:8080/api/auth/login

# Test project routes
curl http://localhost:8080/api/projects
```

## Best Practices

- Keep route configurations simple and maintainable
- Use meaningful route IDs
- Monitor gateway performance and latency
- Implement proper error handling
- Use API versioning in paths
- Document all exposed endpoints

## Integration

The API Gateway integrates with:
- **Discovery Server**: For dynamic service discovery
- **Auth Service**: Routes authentication requests
- **Project Service**: Routes business logic requests
- **External Clients**: Provides unified API interface
