# DurvalCRM J2EE - Build and Deployment Guide

This guide provides comprehensive instructions for building and deploying the DurvalCRM J2EE application to WildFly 37.

## Overview

The DurvalCRM J2EE project includes a complete set of automation scripts for:
- **Building** the Maven-based J2EE application
- **Deploying** to WildFly 37 application server
- **Environment setup** and configuration
- **Development workflows** and utilities
- **Complete CI/CD pipeline** automation

## Project Structure

```
durvalcrm-j2ee/
├── src/                          # Source code
├── target/                       # Build artifacts
├── build.sh                      # Build automation script
├── deploy.sh                     # WildFly deployment script
├── setup-wildfly.sh             # WildFly environment setup
├── pipeline.sh                  # Complete CI/CD pipeline
├── dev-tools.sh                 # Development utilities
├── pom.xml                      # Maven configuration
└── BUILD-DEPLOY-GUIDE.md        # This guide
```

## Prerequisites

### System Requirements
- **Java 17** or higher
- **Maven 3.6+**
- **WildFly 37** application server
- **PostgreSQL 12+** database
- **Linux/Unix** environment (tested on Ubuntu/CentOS)

### Network Requirements
- Access to Maven repositories
- Network connectivity to target deployment server
- Database connectivity (PostgreSQL)

## Quick Start

### 1. Build the Application
```bash
# Quick build without tests
./build.sh --skip-tests

# Full build with tests and reports
./build.sh --profile=prod
```

### 2. Deploy to WildFly
```bash
# Deploy with automatic database configuration
./deploy.sh

# Deploy to specific server
./deploy.sh --wildfly-host=20.127.155.169 --db-host=20.127.155.169
```

### 3. Complete Pipeline
```bash
# Full CI/CD pipeline (build + deploy + verify)
./pipeline.sh --env=production --server=20.127.155.169
```

## Detailed Script Usage

## 1. Build Script (`build.sh`)

Comprehensive Maven build automation with validation and reporting.

### Basic Usage
```bash
./build.sh                    # Default build with dev profile
./build.sh --profile=prod     # Production build
./build.sh --skip-tests       # Skip running tests
./build.sh --clean-only       # Only clean, don't build
```

### Features
- ✅ **Pre-flight checks** - Java, Maven, project structure validation
- ✅ **Source compilation** - MapStruct processing, annotation processing
- ✅ **Test execution** - Unit tests with detailed reporting
- ✅ **WAR packaging** - Optimized for WildFly deployment
- ✅ **Build reporting** - Comprehensive build reports
- ✅ **Artifact validation** - WAR content verification

### Build Profiles
- **dev** (default): Development build with debug information
- **prod**: Production build with optimizations

### Output
- `target/durvalcrm.war` - Deployable WAR file
- `target/build-report.txt` - Detailed build report
- `target/surefire-reports/` - Test reports

## 2. Deployment Script (`deploy.sh`)

Automated WildFly deployment with database configuration.

### Basic Usage
```bash
./deploy.sh                              # Deploy to localhost
./deploy.sh --wildfly-host=server.com    # Deploy to remote server
./deploy.sh --no-build                   # Deploy existing WAR
./deploy.sh --skip-db-config             # Skip database setup
```

### Advanced Configuration
```bash
./deploy.sh \
  --wildfly-host=20.127.155.169 \
  --db-host=20.127.155.169 \
  --db-name=durvalcrm_prod \
  --db-user=durvalcrm_user \
  --db-password=secure_password \
  --force-redeploy
```

### Features
- ✅ **Automatic building** - Builds WAR if needed
- ✅ **PostgreSQL module** - Installs JDBC driver automatically
- ✅ **DataSource configuration** - JTA DataSource setup
- ✅ **Deployment verification** - Health checks and endpoint testing
- ✅ **Rollback capability** - Deployment backups
- ✅ **Connection testing** - Database connectivity validation

### Database Configuration
The script automatically configures:
- PostgreSQL JDBC driver module
- JTA DataSource: `java:jboss/datasources/DurvalCRMDS`
- Connection pool settings
- Transaction isolation

## 3. Environment Setup (`setup-wildfly.sh`)

Complete WildFly environment preparation script.

### Usage (Run as root/sudo)
```bash
sudo ./setup-wildfly.sh                    # Default setup
sudo ./setup-wildfly.sh --enable-ssl       # With SSL/TLS
sudo ./setup-wildfly.sh --no-db-setup      # Skip PostgreSQL
```

### Advanced Options
```bash
sudo ./setup-wildfly.sh \
  --wildfly-version=37.0.0.Final \
  --wildfly-home=/opt/wildfly \
  --bind-address=0.0.0.0 \
  --http-port=8080 \
  --https-port=8443 \
  --management-port=9990
```

### Features
- ✅ **WildFly installation** - Downloads and installs WildFly 37
- ✅ **System user creation** - Dedicated WildFly service user
- ✅ **PostgreSQL setup** - Database server installation and configuration
- ✅ **Systemd service** - System service configuration
- ✅ **Firewall configuration** - Port opening automation
- ✅ **SSL/TLS setup** - Optional certificate configuration
- ✅ **Management scripts** - Service management utilities

### Systemd Service Management
After setup, manage WildFly with:
```bash
systemctl start wildfly     # Start WildFly
systemctl stop wildfly      # Stop WildFly
systemctl restart wildfly   # Restart WildFly
systemctl status wildfly    # Check status
journalctl -u wildfly -f    # View logs
```

## 4. CI/CD Pipeline (`pipeline.sh`)

Complete automation pipeline for enterprise deployments.

### Pipeline Modes
```bash
./pipeline.sh --mode=full           # Complete pipeline
./pipeline.sh --mode=build-only     # Only build and test
./pipeline.sh --mode=deploy-only    # Only deploy (requires WAR)
./pipeline.sh --mode=test-only      # Only run tests
```

### Environment Targeting
```bash
./pipeline.sh --env=development     # Development environment
./pipeline.sh --env=staging         # Staging environment
./pipeline.sh --env=production      # Production environment
```

### Advanced Pipeline Options
```bash
./pipeline.sh \
  --env=production \
  --server=20.127.155.169 \
  --profile=prod \
  --slack-webhook=https://hooks.slack.com/... \
  --email=admin@company.com \
  --skip-tests
```

### Pipeline Stages
1. **Initialization** - Workspace setup, logging configuration
2. **Pre-flight Checks** - Environment validation, connectivity tests
3. **Source Analysis** - Dependency analysis, security scanning
4. **Build** - Compilation, testing, packaging
5. **Deployment** - Backup, deploy, configuration
6. **Verification** - Health checks, endpoint testing
7. **Smoke Tests** - Basic functionality validation
8. **Reporting** - HTML/Text reports generation
9. **Notifications** - Slack/Teams/Email notifications

### Pipeline Features
- ✅ **Multi-environment support** - Development, Staging, Production
- ✅ **Backup and rollback** - Automatic deployment backups
- ✅ **Health monitoring** - Comprehensive health checks
- ✅ **Notification system** - Slack, Teams, Email integration
- ✅ **Detailed reporting** - HTML and text reports
- ✅ **Security scanning** - OWASP dependency checks
- ✅ **Performance baseline** - Response time monitoring

## 5. Development Tools (`dev-tools.sh`)

Development workflow utilities and debugging tools.

### Build Commands
```bash
./dev-tools.sh quick-build         # Fast build without tests
./dev-tools.sh full-build          # Complete build with tests
./dev-tools.sh clean-build         # Clean and rebuild
./dev-tools.sh watch-build         # Auto-rebuild on changes
```

### WildFly Management
```bash
./dev-tools.sh wildfly-start       # Start WildFly
./dev-tools.sh wildfly-stop        # Stop WildFly
./dev-tools.sh wildfly-restart     # Restart WildFly
./dev-tools.sh wildfly-status      # Show status
./dev-tools.sh wildfly-logs        # Tail logs
./dev-tools.sh wildfly-deploy      # Deploy current WAR
./dev-tools.sh wildfly-redeploy    # Redeploy application
```

### Database Utilities
```bash
./dev-tools.sh db-status           # Check database connection
./dev-tools.sh db-reset            # Reset database schema
./dev-tools.sh db-backup           # Backup database
./dev-tools.sh db-console          # Open database console
./dev-tools.sh gen-test-data       # Generate test data
```

### Development Utilities
```bash
./dev-tools.sh dev-setup           # Setup dev environment
./dev-tools.sh api-test            # Test API endpoints
./dev-tools.sh health-check        # Check application health
./dev-tools.sh project-info        # Show project information
./dev-tools.sh env-check           # Validate environment
./dev-tools.sh cleanup             # Clean temporary files
```

## Configuration

### Environment Variables

Set these environment variables for customized behavior:

```bash
# WildFly Configuration
export WILDFLY_HOME=/opt/wildfly
export WILDFLY_USER=admin
export WILDFLY_PASSWORD=admin123

# Database Configuration
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=durvalcrm_dev
export DB_USER=durvalcrm_user
export DB_PASSWORD=durvaldev@123

# Build Configuration
export BUILD_PROFILE=prod
export SKIP_TESTS=false

# Deployment Configuration
export DEPLOY_SERVER=20.127.155.169
export TARGET_ENV=production
```

### Configuration Files

After running `dev-tools.sh dev-setup`, you'll have:
- `dev-config.properties` - Development configuration
- Test data SQL scripts
- Log directories

## Deployment Scenarios

### Local Development Deployment
```bash
# 1. Setup local environment
sudo ./setup-wildfly.sh --wildfly-home=/opt/wildfly

# 2. Build and deploy
./build.sh --profile=dev
./deploy.sh --wildfly-host=localhost

# 3. Verify deployment
./dev-tools.sh health-check
```

### Production Deployment
```bash
# 1. Complete production pipeline
./pipeline.sh \
  --env=production \
  --server=20.127.155.169 \
  --profile=prod \
  --slack-webhook=https://hooks.slack.com/your-webhook

# 2. Monitor deployment
curl http://20.127.155.169:8080/durvalcrm/api/health
```

### Staging Environment
```bash
# Build and deploy to staging
./pipeline.sh \
  --env=staging \
  --server=staging.company.com \
  --profile=prod \
  --email=team@company.com
```

## Troubleshooting

### Common Issues

#### Build Failures
```bash
# Check Java version
java -version

# Validate Maven configuration
mvn validate

# Clean and rebuild
./build.sh --clean-only
./build.sh --profile=dev
```

#### Deployment Issues
```bash
# Check WildFly status
./dev-tools.sh wildfly-status

# Check ports
./dev-tools.sh port-check

# View WildFly logs
./dev-tools.sh wildfly-logs
```

#### Database Connection Problems
```bash
# Test database connection
./dev-tools.sh db-status

# Check PostgreSQL service
sudo systemctl status postgresql

# Verify database configuration
./dev-tools.sh db-console
```

#### Network Connectivity
```bash
# Test server connectivity
ping 20.127.155.169

# Check WildFly management interface
curl http://20.127.155.169:9990

# Test application endpoints
./dev-tools.sh api-test
```

### Log Files

Important log files for troubleshooting:

- **WildFly Server**: `/opt/wildfly/standalone/log/server.log`
- **Systemd Service**: `journalctl -u wildfly`
- **Pipeline Logs**: `pipeline-workspace/pipeline-*.log`
- **Build Reports**: `target/build-report.txt`
- **Deployment Reports**: `deployment-report.txt`

### Performance Monitoring

Monitor application performance:

```bash
# Check response times
curl -w "@curl-format.txt" http://server:8080/durvalcrm/api/health

# Monitor JVM metrics (if enabled)
curl http://server:9990/management

# Database performance
./dev-tools.sh db-console
# Then run: SELECT * FROM pg_stat_activity;
```

## API Endpoints

After successful deployment, the following endpoints are available:

### Health and Monitoring
- `GET /durvalcrm/api/health` - Application health check

### Business APIs
- `GET /durvalcrm/api/associados` - Members management
- `GET /durvalcrm/api/mensalidades` - Monthly payments
- `GET /durvalcrm/api/doacoes` - Donations management
- `GET /durvalcrm/api/vendas` - Sales management
- `GET /durvalcrm/api/dashboard` - Dashboard data

### Example API Testing
```bash
# Health check
curl http://20.127.155.169:8080/durvalcrm/api/health

# Get members (may require authentication)
curl http://20.127.155.169:8080/durvalcrm/api/associados

# Automated API testing
./dev-tools.sh api-test
```

## Security Considerations

### Production Deployment Security
- Change default WildFly admin credentials
- Use proper SSL/TLS certificates
- Configure firewall rules appropriately
- Use strong database passwords
- Enable WildFly security realm for API authentication

### Database Security
- Use dedicated database user with minimal privileges
- Enable SSL for database connections
- Regular database backups
- Monitor database access logs

## Backup and Recovery

### Automated Backups
The deployment scripts automatically create backups:
- Previous WAR files before deployment
- WildFly configuration files
- Database dumps (if enabled)

### Manual Backup
```bash
# Backup database
./dev-tools.sh db-backup

# Backup WildFly configuration
cp /opt/wildfly/standalone/configuration/standalone.xml backup/

# Backup current WAR
cp /opt/wildfly/standalone/deployments/durvalcrm.war backup/
```

### Recovery Procedures
```bash
# Restore from backup
./deploy.sh --force-redeploy

# Restore database from backup
./dev-tools.sh db-reset
# Then restore from SQL dump

# Rollback to previous deployment
# Use backup files in pipeline-workspace/backups/
```

## Support and Maintenance

### Regular Maintenance Tasks
1. **Update dependencies** - Run `./dev-tools.sh update-deps` monthly
2. **Security scans** - Run `./dev-tools.sh security-scan` weekly
3. **Database maintenance** - Regular cleanup and optimization
4. **Log rotation** - Configure log rotation for WildFly logs
5. **Performance monitoring** - Monitor response times and resource usage

### Getting Help
- Check script help: `./script-name.sh --help`
- View comprehensive logs in `pipeline-workspace/`
- Test individual components with `dev-tools.sh`
- Verify environment with `dev-tools.sh env-check`

## Script Customization

All scripts are designed to be customizable through:
- Environment variables
- Command-line parameters
- Configuration files
- Script modification (all scripts are well-documented)

For advanced customization, modify the scripts according to your specific requirements. Each script includes detailed comments and modular functions for easy modification.

---

This guide provides comprehensive coverage of the DurvalCRM J2EE build and deployment automation. For specific use cases or advanced configurations, refer to the individual script help documentation using the `--help` parameter.