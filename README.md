# DurvalCRM J2EE

This is the J2EE-compliant version of DurvalCRM, converted from the original Quarkus application for deployment on WildFly 37 running at https://20.127.155.169:8443/

## Overview

DurvalCRM is a membership management system for associations, managing members (associados) and their monthly payments (mensalidades). This version has been fully converted to standard J2EE specifications and is compatible with WildFly 37.

## Architecture

The application follows clean architecture principles with clear separation of concerns:

- **Domain Layer**: Business entities and repository interfaces (no changes from original)
- **Application Layer**: Use cases, DTOs, mappers, and application services
- **Infrastructure Layer**: JPA entities, repository implementations, REST resources, and schedulers

## Key Conversion Changes

### From Quarkus to J2EE:

1. **Dependencies**: Converted from Quarkus dependencies to standard J2EE 8 APIs
2. **Annotations**: Changed from `jakarta.*` to `javax.*` imports for WildFly compatibility
3. **Panache to JPA**: Replaced Quarkus Panache with standard JPA EntityManager operations
4. **Scheduler**: Converted from Quarkus `@Scheduled` to EJB `@Schedule`
5. **Security**: Removed all authentication/authorization restrictions as requested
6. **Build**: Changed from Quarkus build to standard WAR packaging

### Database Configuration:

- Uses JTA DataSource: `java:jboss/datasources/DurvalCRMDS`
- PostgreSQL driver (to be configured in WildFly)
- JPA Persistence Unit: `durvalcrm-pu`

## WildFly Deployment Requirements

### 1. DataSource Configuration

Configure a PostgreSQL DataSource in WildFly with JNDI name: `java:jboss/datasources/DurvalCRMDS`

Example CLI commands:
```bash
/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-class-name=org.postgresql.Driver)

/subsystem=datasources/data-source=DurvalCRMDS:add(jndi-name=java:jboss/datasources/DurvalCRMDS,driver-name=postgresql,connection-url=jdbc:postgresql://localhost:5432/durvalcrm_dev,user-name=durvalcrm_user,password=durvaldev@123)

/subsystem=datasources/data-source=DurvalCRMDS:enable
```

### 2. PostgreSQL Driver Module

Install PostgreSQL JDBC driver as a WildFly module or deploy as a driver deployment.

### 3. Application Deployment

1. Build the WAR file: `mvn clean package`
2. Deploy the generated `target/durvalcrm.war` to WildFly
3. Application will be available at: `https://20.127.155.169:8443/durvalcrm/`

## API Endpoints

All REST endpoints are accessible without authentication (as requested):

- **Members**: `/durvalcrm/api/associados`
- **Monthly Payments**: `/durvalcrm/api/mensalidades`  
- **Donations**: `/durvalcrm/api/doacoes`
- **Sales**: `/durvalcrm/api/vendas`
- **Dashboard**: `/durvalcrm/api/dashboard`
- **Health Check**: `/durvalcrm/api/health`

## Business Logic

1. **Monthly Payment Generation**: EJB scheduled job runs monthly to create R$10.90 charges for active members
2. **Payment Status**: Automatically transitions from PENDENTE → VENCIDA after due date  
3. **PIX Integration**: Generates QR codes for payment (mock implementation)
4. **Member Management**: CRUD operations with soft delete (marks as inactive)

## Database Schema

- **associados**: Member information
- **mensalidades**: Monthly payment records
- **doacoes**: Donation records  
- **vendas**: Sales records from cantina, bazaar, books

## Build Commands

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package WAR
mvn clean package

# The generated durvalcrm.war will be in target/ directory
```

## Technology Stack

- **J2EE 8**: Core platform
- **JPA 2.2**: Database persistence
- **JAX-RS 2.1**: REST services
- **CDI 2.0**: Dependency injection
- **EJB 3.2**: Scheduled jobs
- **Bean Validation 2.0**: Input validation
- **PostgreSQL**: Database
- **MapStruct**: DTO mapping
- **Lombok**: Code generation

## Notes

- All security restrictions have been removed as requested
- Application context root: `/durvalcrm`
- WAR file name: `durvalcrm.war`
- Compatible with WildFly 37 and similar J2EE 8 application servers
- Clean architecture maintained from original Quarkus version