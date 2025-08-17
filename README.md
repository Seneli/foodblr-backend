# foodblr-backend
Back end for Foodblr

## Prerequisites

- Java 21+
- PostgreSQL 14+ (or Docker for containerized PostgreSQL)
- Gradle 8+ (or use the included Gradle wrapper)

## Database Configuration

This project uses PostgreSQL with Liquibase for database migrations. The default configuration expects:
- **Host**: `localhost:5432`
- **Database**: `foodblr` (development), `foodblr_test` (testing)
- **Username**: `postgres`
- **Password**: *(empty/no password)*

## Gradle Commands

### 🚀 Application Commands

```bash
# Run the Spring Boot application
./gradlew bootRun

# Run with development profile (uses H2 in-memory database)
./gradlew bootRun --args='--spring.profiles.active=dev'

# Build the application
./gradlew build

# Clean build artifacts
./gradlew clean

# Run tests
./gradlew test

# Generate JAR file
./gradlew jar
```

### 🗄️ Database Commands

#### Database Setup
```bash
# Create development database
./gradlew createDatabases

# Create test database
./gradlew createTestDatabase

# Setup both development and test databases with migrations
./gradlew setupDatabases
```

#### Database Migrations
```bash
# Run Liquibase migrations on development database
./gradlew migrateDevDatabase

# Run Liquibase migrations on test database
./gradlew migrateTestDatabase

# Run migrations manually (development database)
./gradlew update

# Check migration status
./gradlew status

# Generate changelog from existing database
./gradlew generateChangeLog
```

#### Liquibase Commands
```bash
# Update database to latest changeset
./gradlew update

# Check status of database migrations
./gradlew status

# Rollback last changeset
./gradlew rollback-count -PliquibaseCommandValue=1

# Rollback to specific tag
./gradlew rollback-tag -PliquibaseCommandValue=your-tag

# Validate changelog
./gradlew validate

# Clear checksums (use with caution)
./gradlew clear-checksums

# Mark next changeset as executed without running it
./gradlew mark-next-changeset-ran

# Generate SQL for update (don't execute)
./gradlew update-sql

# Show pending changesets
./gradlew status --verbose
```

### 🔧 Development Commands

```bash
# Build and run without tests
./gradlew bootRun -x test

# Run with different log levels
./gradlew bootRun --args='--logging.level.liquibase=DEBUG'

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Check dependencies
./gradlew dependencies

# Show project tasks
./gradlew tasks

# Show database-specific tasks
./gradlew tasks --group database
```

### 📊 Code Quality & Analysis

```bash
# Run all tests
./gradlew test

# Run tests with detailed output
./gradlew test --info

# Generate test report
./gradlew test jacocoTestReport
```

## Quick Start

1. **Setup PostgreSQL** (choose one):
   ```bash
   # Option A: Using Docker
   docker run --name foodblr-postgres \
     -e POSTGRES_DB=postgres \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD= \
     -p 5432:5432 \
     -d postgres:15

   # Option B: Using Homebrew
   brew install postgresql@15
   brew services start postgresql@15
   ```

2. **Initialize Database**:
   ```bash
   ./gradlew setupDatabases
   ```

3. **Run the Application**:
   ```bash
   ./gradlew bootRun
   ```

4. **Access the Application**:
   - Application: http://localhost:8080
   - H2 Console (dev profile): http://localhost:8080/h2-console

## Project Structure

```
src/main/resources/db/changelog/
├── db.changelog-master.xml          # Master changelog file
└── changesets/
    └── 001-create-user-table.sql    # Individual migration files
```

## Database Profiles

- **Default Profile**: PostgreSQL database (`application.properties`)
- **Dev Profile**: H2 in-memory database (`application-dev.properties`)

## Environment Variables

You can override database configuration using environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
