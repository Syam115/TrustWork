# Discovery Server

The Discovery Server is a service registry that enables service discovery in the TrustWork microservices architecture. It uses Netflix Eureka to maintain a registry of all running services and their locations, allowing services to find and communicate with each other dynamically.

## Features

- Service registration and discovery
- Load balancing support
- Service health monitoring
- Dynamic service location resolution
- Eureka Dashboard for monitoring

## Technology Stack

- **Spring Boot 3.2.5**
- **Spring Cloud Netflix Eureka Server**
- **Spring Web**

## Configuration

The service runs on port **8761** and does not require a database.

Key configurations in `application.yaml`:
- Server Port: 8761
- Eureka Client: Disabled registration and registry fetching (server mode)
- Application Name: DISCOVERY-SERVER

## Eureka Dashboard

Once running, access the Eureka Dashboard at: `http://localhost:8761`

The dashboard shows:
- Registered services
- Service instances
- Service health status
- Last updated timestamps

## How It Works

### Service Registration
- Each microservice registers itself with the Discovery Server on startup
- Services send heartbeat signals to maintain registration
- If a service fails to send heartbeats, it gets removed from the registry

### Service Discovery
- Services query the Discovery Server to find other services
- Load balancing is achieved through multiple instances of the same service
- Services communicate using logical names instead of hardcoded URLs

## Running the Service

### Prerequisites
- Java 21+
- Maven 3.6+

### Start Command
```bash
cd discovery-server
mvn spring-boot:run
```

### Build JAR
```bash
mvn clean package
java -jar target/discovery-server-1.0-SNAPSHOT.jar
```

## Dependencies

Key dependencies include:
- `spring-boot-starter-web`: Web framework
- `spring-cloud-starter-netflix-eureka-server`: Eureka server functionality

## Development

### Project Structure
```
discovery-server/
├── src/main/java/io/eikon/discoveryserver/
│   └── DiscoveryServerApplication.java
├── src/main/resources/
│   └── application.yaml
└── pom.xml
```

### Main Application Class
```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}
```

## Service Registration Process

1. **Startup**: Services register with Eureka Server
2. **Heartbeat**: Services send periodic heartbeats (default: 30 seconds)
3. **Discovery**: Services query Eureka for other service locations
4. **Load Balancing**: Ribbon (via Eureka) distributes requests

## Configuration for Client Services

Other services register with this Discovery Server using:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}
```

## Monitoring

### Eureka Dashboard Features
- **Instances currently registered**: Shows all registered services
- **General Info**: Server information and status
- **Instance Info**: Details about each service instance
- **Health Check**: Service availability status

### Common Issues
- **Service not appearing**: Check if service is configured to register
- **Service disappearing**: Check network connectivity and heartbeats
- **Load balancing not working**: Verify multiple instances are registered

## Integration

This service is the foundation for the microservices architecture and integrates with:
- **Auth Service**: Registers for authentication operations
- **Project Service**: Registers for project management
- **API Gateway**: Registers for request routing

## Best Practices

- Start Discovery Server before other services
- Monitor the Eureka Dashboard regularly
- Configure appropriate heartbeat intervals
- Use logical service names in configurations
- Implement circuit breakers for resilient communication

## Security Considerations

For production deployments, consider:
- Securing the Eureka Dashboard with authentication
- Using HTTPS for service communication
- Network security between services
- Service authentication mechanisms

## Troubleshooting

### Service Registration Issues
```bash
# Check if service is running
curl http://localhost:8761/eureka/apps

# Check service logs for registration errors
# Verify eureka.client.service-url.defaultZone configuration
```

### Common Errors
- **Connection refused**: Discovery Server not running
- **Service not found**: Service not registered or network issues
- **Load balancer errors**: No healthy instances available
