# FF14 Static Maker - Spring Boot REST API

A Spring Boot REST API for managing Final Fantasy 14 raiding groups (called "statics"). This application allows you to create raid groups, add players to groups, validate player existence via FFLogs API, and export group information.

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.11** (Latest Stable)
- **Maven** - Build tool
- **MySQL 8.0** - Database
- **Docker & Docker Compose** - Containerization
- **Spring Data JPA** - Database access
- **Spring Security** - Security framework (currently configured for open access)
- **Lombok** - Reduce boilerplate code
- **Spring Boot Actuator** - Monitoring and health checks

## Architecture

The application follows a layered REST API architecture:

```
Controller (REST API) → Service (Business Logic) → Repository (Data Access) → Database
```

### Key Components

- **Models (JPA Entities)**: `Player`, `RaidGroup`, `RegionStats`, `RegionType`
- **Repositories**: Spring Data JPA repositories for database operations
- **Services**: Business logic including FFLogs character validation
- **Controllers**: REST API endpoints
- **Exception Handling**: Global exception handler for consistent error responses

## Getting Started

### Prerequisites

- Docker and Docker Compose installed
- (Optional) Java 17 and Maven if running outside Docker

### Running with Docker (Recommended)

1. **Clone the repository and navigate to the project directory**

2. **Copy the environment template**
   ```bash
   cp .env.example .env
   ```

3. **Start the application**
   ```bash
   cd .docker/dev
   docker compose up --build
   ```

   The application will be available at:
   - API: http://localhost:8080
   - Actuator: http://localhost:8080/actuator
   - MySQL: localhost:3306

4. **Remote Debugging (IntelliJ IDEA)**
   - Go to `Run > Edit Configurations > Remote JVM Debug`
   - Debugger mode: Attach to remote JVM
   - Host: localhost
   - Port: 5005
   - Start debugging

### Running Locally (Without Docker)

1. **Start MySQL** (ensure it's running on localhost:3306)

2. **Create database**
   ```sql
   CREATE DATABASE fflog;
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

### Player Management

- `POST /api/players` - Create a new player
  ```json
  {
    "firstname": "John",
    "surname": "Doe",
    "role": "Tank",
    "datacenter": "Aether",
    "server": "Gilgamesh",
    "staticName": "MyStatic"
  }
  ```

- `GET /api/players` - Get all players
- `GET /api/players/static/{staticName}` - Get players by static name
- `GET /api/players/region/{region}` - Get players by region (EUROPE, AMERICA, JAPAN)
- `DELETE /api/players/{playerId}` - Delete a player
- `PATCH /api/players/{playerId}/role?role={newRole}` - Update player role

### Raid Group Management

- `POST /api/raid-groups` - Create a new raid group
  ```json
  {
    "groupName": "MyStatic",
    "region": "EUROPE"
  }
  ```

- `GET /api/raid-groups` - Get all raid groups
- `GET /api/raid-groups/{groupName}` - Get raid group by name (with members)
- `GET /api/raid-groups/region/{region}` - Get raid groups by region
- `DELETE /api/raid-groups/{groupName}` - Delete a raid group
- `GET /api/raid-groups/{groupName}/export` - Export raid group as text

### Region Statistics

- `GET /api/region-stats` - Get statistics for all regions
- `GET /api/region-stats/{region}` - Get statistics for a specific region

### Health & Monitoring

- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

## Configuration

Configuration is managed through environment variables. See `.env.example` for all available options:

| Variable | Description | Default |
|----------|-------------|---------|
| `WEB_PORT` | Application port | 8080 |
| `MYSQL_DATABASE` | Database name | fflog |
| `MYSQL_USER` | Database user | fflog_user |
| `MYSQL_PASSWORD` | Database password | fflog_password |
| `MYSQL_ROOT_PASSWORD` | MySQL root password | rootpassword |
| `LOG_LEVEL` | Application log level | DEBUG |
| `SHOW_SQL` | Show SQL queries in logs | false |

## Database Schema

The application uses JPA to automatically create the following tables:

- **players** - Player information with FK to raid_groups
- **raid_groups** - Raid group metadata
- **region_stats** - Statistics by region

## Features

### Player Validation

- Name length ≤ 15 characters
- Only letters allowed (no spaces or special characters)
- No duplicate names within the same static
- Character existence validated against FFLogs.com

### Region Mapping

The application automatically maps datacenters to regions:

- **Europe**: Chaos, Light
- **America**: Aether, Crystal, Dynamis, Primal
- **Japan**: Elemental, Gaia, Mana, Meteor

### Automatic Statistics Tracking

Player and raid group counts are automatically maintained per region.

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/fflog/staticmaker/
│   │   ├── config/          # Spring configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/      # Custom exceptions and handlers
│   │   ├── model/          # JPA entities
│   │   ├── repository/     # Spring Data repositories
│   │   └── service/        # Business logic
│   └── resources/
│       └── application.yml  # Application configuration
└── test/                    # Unit and integration tests
```

### Building for Production

```bash
mvn clean package -DskipTests
```

The JAR file will be in `target/staticmaker-1.0.0.jar`

### Running Tests

```bash
mvn test
```

## Docker Configuration

### Development

The development setup includes:
- Hot reload with Spring Boot DevTools
- Remote debugging on port 5005
- Source code mounted as volume for live updates
- Maven dependency caching

### Production

Build production image:
```bash
docker build -f .docker/dev/Dockerfile --target production -t staticmaker:latest .
```

## Migration from JavaFX

This Spring Boot version replaces the original JavaFX desktop application with a REST API. Key changes:

- JavaFX UI → REST API endpoints
- JDBC → Spring Data JPA
- Direct SQL → Repository methods with query methods
- String concatenation → PreparedStatements (via JPA)
- Swing dialogs → JSON error responses
- Single application → Microservice-ready architecture

## Original JavaFX Application

The original JavaFX desktop application is still available in the `ProjectFFLOG/` directory.

## License

Private project for learning purposes.
