# DurvalCRM Backend (Jakarta EE)

> Membership management system for associations - Backend API built with Jakarta EE 10 for WildFly 37

## Overview

DurvalCRM is a comprehensive membership management system designed for associations, managing:
- **Members** (associados) - Complete member lifecycle and information
- **Monthly Payments** (mensalidades) - Automated billing and payment tracking
- **Donations** (doações) - Voluntary contributions tracking
- **Sales** (vendas) - Cantina, bazaar, and book sales

This is the Jakarta EE version, converted from the original Quarkus application for enterprise deployment on WildFly 37 application server.

## Technology Stack

- **Jakarta EE 10** - Enterprise Java specification
- **Java 17** - Language version
- **WildFly 37** - Application server
- **PostgreSQL 12+** - Relational database
- **JPA 2.2 / Hibernate 6.2** - Object-relational mapping
- **JAX-RS 2.1** - RESTful web services
- **CDI** - Dependency injection
- **EJB @Schedule** - Scheduled jobs for automated billing
- **MapStruct 1.6** - DTO to entity mapping
- **Lombok** - Code generation
- **Maven** - Build tool
- **JUnit 4.13 + Mockito 5.4** - Testing framework
- **JaCoCo** - Code coverage reporting

## Architecture

The application follows **Clean Architecture / Domain-Driven Design** principles with clear separation of concerns:

```
br.org.cecairbar.durvalcrm/
├── domain/                    # Core business layer
│   ├── model/                 # Domain entities (Associado, Mensalidade, Doacao, Venda)
│   └── repository/            # Repository interfaces (contracts)
├── application/               # Application logic layer
│   ├── usecase/               # Business use cases (CreateAssociado, GenerateMensalidade, etc.)
│   ├── dto/                   # Data Transfer Objects for API communication
│   ├── mapper/                # MapStruct mappers (DTO ↔ Entity conversion)
│   └── service/               # Application services (PIX, email, etc.)
└── infrastructure/            # External concerns layer
    ├── web/resource/          # JAX-RS REST endpoints (@Path controllers)
    ├── persistence/           # JPA entities & repository implementations
    └── scheduler/             # EJB scheduled jobs (monthly billing automation)
```

**Key Design Patterns:**
- Repository pattern with JPA EntityManager
- Use case pattern for business operations
- DTO pattern for API contracts
- Dependency injection via CDI
- Soft delete pattern for data retention

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 12+ running (or via Podman/Docker)
- WildFly 37 application server

### Local Development Setup

1. **Start Database and Keycloak** (via Podman Compose):
   ```bash
   # From project root
   podman-compose up -d
   ```

2. **Build the Application**:
   ```bash
   mvn clean package
   ```

3. **Deploy to WildFly**:
   ```bash
   mvn wildfly:deploy
   ```

4. **Access the API**:
   - Base URL: `http://localhost:9991/durvalcrm/`
   - API Endpoints: `http://localhost:9991/durvalcrm/api/v1/`
   - Health Check: `http://localhost:9991/durvalcrm/api/v1/health`

## Maven Commands

### Build & Package
```bash
# Clean and compile
mvn clean compile

# Run unit tests
mvn test

# Run specific test
mvn test -Dtest=AssociadoUseCaseTest

# Generate test coverage report (JaCoCo)
mvn test jacoco:report
# Report available at: target/site/jacoco/index.html

# Package WAR file
mvn clean package
# Output: target/durvalcrm.war
```

### Deployment
```bash
# Deploy to local WildFly (development)
mvn wildfly:deploy

# Deploy to staging environment
mvn clean package wildfly:deploy -Pstaging

# Deploy to production environment
mvn clean package wildfly:deploy -Pproduction
```

## Deployment Environments

### Development (Local)
- **URL**: http://localhost:9991/durvalcrm/
- **Database**: PostgreSQL via Podman Compose
- **Profile**: `development` (default)

### Staging (Vagrant VM)
- **URL**: http://localhost:9443/durvalcrm/ (SSH tunnel)
- **Profile**: `staging`
- **Deployment**: SSH-based via Maven profile

### Production (Cloud VPS)
- **Domain**: https://cecairbar.org.br, https://durvalcrm.org
- **Profile**: `production`
- **Deployment**: Remote deployment via Maven + shell scripts

See `BUILD-DEPLOY-GUIDE.md` for comprehensive deployment documentation.

## API Documentation

**Base URL Pattern**: `https://[domain]/durvalcrm/api/v1/`

### Endpoints

#### Members (Associados)
- `GET    /associados` - List members with pagination and search
- `GET    /associados/{id}` - Get member by ID
- `POST   /associados` - Create new member
- `PUT    /associados/{id}` - Update member
- `DELETE /associados/{id}` - Soft delete (sets ativo=false)

#### Monthly Payments (Mensalidades)
- `GET  /mensalidades` - List monthly payments with filters
- `GET  /mensalidades/{id}` - Get payment by ID
- `POST /mensalidades/gerar` - Generate monthly charges for all active members
- `PUT  /mensalidades/{id}` - Update payment status
- `GET  /mensalidades/associado/{associadoId}` - Get payments for specific member

#### Donations (Doações)
- `GET  /doacoes` - List donations
- `GET  /doacoes/{id}` - Get donation by ID
- `POST /doacoes` - Register new donation
- `PUT  /doacoes/{id}` - Update donation

#### Sales (Vendas)
- `GET  /vendas` - List sales
- `GET  /vendas/{id}` - Get sale by ID
- `POST /vendas` - Register new sale
- `PUT  /vendas/{id}` - Update sale

#### Analytics
- `GET /dashboard` - Dashboard metrics (members, payments, donations, sales)

#### Health & Monitoring
- `GET /health` - Health check endpoint

**Note**: All endpoints are currently **public** (no authentication required). The frontend uses Keycloak for authentication, but the backend does not enforce it.

## Business Logic

### Automated Monthly Billing
- EJB `@Schedule` job runs on the 1st day of each month at 3:00 AM
- Generates R$10.90 charges for all **active** members
- Creates `Mensalidade` records with status `PENDENTE`
- Due date: 10th of the reference month
- Unique constraint: One payment per member per month

### Payment Status Flow
- `PENDENTE` → Initial status when generated
- `VENCIDA` → Automatically set after due date passes
- `PAGA` → Manually set when payment is confirmed
- `CANCELADA` → Cancelled payment

### PIX Integration
- Generates PIX QR codes for payments
- Currently uses mock implementation
- Ready for integration with real PIX provider

### Data Integrity
- CPF validation enforced
- Soft delete pattern (preserves data)
- Referential integrity via JPA relationships

## Database Schema

### Key Tables

**associados** - Member information
- nome_completo, cpf, email, telefone
- data_nascimento, data_entrada
- endereco_completo
- ativo (boolean for soft delete)

**mensalidades** - Monthly payment records
- associado_id (FK)
- mes_referencia, ano_referencia
- valor, data_vencimento, data_pagamento
- status (PENDENTE, PAGA, VENCIDA, CANCELADA)
- UNIQUE constraint on (associado_id, mes_referencia, ano_referencia)

**doacoes** - Donation records
- associado_id (nullable FK)
- valor, data_doacao
- descricao, mensalidade_id (optional link)

**vendas** - Sales records
- associado_id (nullable FK)
- tipo (CANTINA, BAZAR, LIVRO)
- valor, data_venda
- descricao

### Database Configuration
- **JNDI Name**: `java:jboss/datasources/DurvalCRMDS`
- **Persistence Unit**: `durvalcrm-pu`
- **Schema Management**: JPA auto-update (use Flyway/Liquibase for production)

## Testing

### Running Tests
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=AssociadoUseCaseTest

# With coverage report
mvn test jacoco:report
```

### Test Configuration
- **Framework**: JUnit 4.13
- **Mocking**: Mockito 5.4
- **Test Database**: H2 in-memory
- **Coverage Tool**: JaCoCo
- **Minimum Coverage**: 30% line, 25% branch

### Coverage Exclusions
- DTOs (`**/dto/**`)
- MapStruct generated mappers (`**/mapper/**/*Impl.class`)
- JPA entities (`**/persistence/entity/**`)
- Infrastructure repositories (`**/infrastructure/repository/**`)

## WildFly Configuration

### DataSource Setup

Configure PostgreSQL DataSource via WildFly CLI:

```bash
# 1. Add PostgreSQL driver
/subsystem=datasources/jdbc-driver=postgresql:add( \
    driver-name=postgresql, \
    driver-module-name=org.postgresql, \
    driver-class-name=org.postgresql.Driver \
)

# 2. Create DataSource
/subsystem=datasources/data-source=DurvalCRMDS:add( \
    jndi-name=java:jboss/datasources/DurvalCRMDS, \
    driver-name=postgresql, \
    connection-url=jdbc:postgresql://localhost:5432/durvalcrm_dev, \
    user-name=durvalcrm_user, \
    password=durvaldev@123 \
)

# 3. Enable DataSource
/subsystem=datasources/data-source=DurvalCRMDS:enable

# 4. Test connection
/subsystem=datasources/data-source=DurvalCRMDS:test-connection-in-pool
```

### PostgreSQL Driver Installation

Option 1: As WildFly module (recommended)
```bash
# Create module directory
mkdir -p $WILDFLY_HOME/modules/org/postgresql/main

# Copy driver JAR
cp postgresql-42.7.3.jar $WILDFLY_HOME/modules/org/postgresql/main/

# Create module.xml (see WildFly documentation)
```

Option 2: Deploy as driver deployment
```bash
cp postgresql-42.7.3.jar $WILDFLY_HOME/standalone/deployments/
```

## Migration from Quarkus

### Key Changes

1. **Dependencies**: Quarkus → Jakarta EE 10 APIs
2. **Imports**: Uses `jakarta.*` (not `javax.*`) for Jakarta EE 10
3. **Panache → JPA**: Replaced Quarkus Panache with standard JPA EntityManager
4. **Scheduler**: Quarkus `@Scheduled` → EJB `@Schedule`
5. **Security**: All authentication/authorization removed (public API)
6. **Build**: Quarkus build → Standard WAR packaging
7. **Context Root**: Configured in `jboss-web.xml`

### Why Jakarta EE?
- Enterprise-grade stability and support
- Wide application server compatibility (WildFly, Payara, WebLogic, etc.)
- Standard specifications (no vendor lock-in)
- Long-term support and maintenance

## Project Structure

```
durvalcrm-j2ee/
├── src/
│   ├── main/
│   │   ├── java/br/org/cecairbar/durvalcrm/
│   │   │   ├── domain/              # Business domain layer
│   │   │   ├── application/         # Application logic
│   │   │   └── infrastructure/      # Technical infrastructure
│   │   ├── resources/
│   │   │   ├── META-INF/
│   │   │   │   ├── persistence.xml  # JPA configuration
│   │   │   │   └── beans.xml        # CDI configuration
│   │   │   └── application.properties
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           ├── web.xml          # Servlet configuration
│   │           └── jboss-web.xml    # WildFly-specific config
│   └── test/
│       ├── java/                    # Unit tests
│       └── resources/
│           └── META-INF/
│               └── persistence.xml  # Test persistence config (H2)
├── pom.xml                          # Maven configuration
├── BUILD-DEPLOY-GUIDE.md           # Deployment documentation
└── README.md                        # This file
```

## Configuration Files

### persistence.xml
- Defines JPA persistence unit `durvalcrm-pu`
- References JNDI DataSource
- Hibernate-specific configurations

### jboss-web.xml
- Context root: `/durvalcrm`
- Security domain configuration (currently unused)

### pom.xml
- Maven profiles: development, staging, production
- Dependencies: Jakarta EE 10 BOM, Hibernate, PostgreSQL, MapStruct, Lombok
- Build configuration: WAR packaging, compiler settings, test coverage

## Important Notes

- **Java Version**: Requires Java 17 (LTS)
- **No Authentication**: All API endpoints are currently public
- **Soft Deletes**: Members are marked inactive, not physically deleted
- **Date Format**: ISO 8601 (YYYY-MM-DD) for all dates
- **CPF Validation**: Enforced on backend via Bean Validation
- **MapStruct Order**: Lombok → Lombok-MapStruct binding → MapStruct processor
- **Context Root**: `/durvalcrm` (configured in jboss-web.xml)
- **WAR Name**: `durvalcrm.war`

## Troubleshooting

### Common Issues

**Issue**: `java.sql.SQLException: No suitable driver found`
- **Fix**: Ensure PostgreSQL JDBC driver is installed in WildFly

**Issue**: `javax.naming.NameNotFoundException: java:jboss/datasources/DurvalCRMDS`
- **Fix**: Configure DataSource in WildFly (see WildFly Configuration section)

**Issue**: MapStruct mappers not generating
- **Fix**: Ensure annotation processor order in pom.xml (Lombok first)

**Issue**: Tests failing with H2 compatibility errors
- **Fix**: Check H2 compatibility mode in test persistence.xml

## Contributing

1. Follow Clean Architecture principles
2. Write unit tests for new use cases (minimum 30% coverage)
3. Use MapStruct for DTO mapping (no manual mapping)
4. Follow Jakarta EE specifications (no proprietary APIs)
5. Update API documentation for new endpoints
6. Test on WildFly 37 before committing

## License

Proprietary - CECA Irbar Association

## Related Projects

- **durvalcrm-frontend**: Vue 3 + TypeScript frontend application
- **Infrastructure**: Podman Compose for local development, Ansible for deployment

## Support

For issues, questions, or contributions, contact the development team.