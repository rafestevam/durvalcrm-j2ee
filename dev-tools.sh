#!/bin/bash

# =============================================================================
# DurvalCRM J2EE Development Tools and Utilities
# =============================================================================
# Description: Development workflow utilities for DurvalCRM J2EE project
# Purpose: Streamline common development tasks and debugging
# Author: Development utilities script
# Version: 1.0.0
# =============================================================================

set -e

# Script configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="DurvalCRM J2EE Dev Tools"

# Configuration
WILDFLY_HOME="${WILDFLY_HOME:-/opt/wildfly}"
WILDFLY_HOST="${WILDFLY_HOST:-localhost}"
WILDFLY_PORT="${WILDFLY_PORT:-9990}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-durvalcrm_dev}"
DB_USER="${DB_USER:-durvalcrm_user}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_header() {
    echo -e "${CYAN}========================================${NC}"
    echo -e "${CYAN}$1${NC}"
    echo -e "${CYAN}========================================${NC}"
}

# Show help
show_help() {
    cat << EOF
================================================================================
                        $PROJECT_NAME - Help
================================================================================

Usage: $0 <command> [options]

DEVELOPMENT COMMANDS:
  quick-build         Quick build without tests
  full-build          Full build with tests and reports
  clean-build         Clean and full rebuild
  watch-build         Watch for changes and auto-rebuild
  
BUILD UTILITIES:
  check-deps          Check and analyze dependencies
  update-deps         Update Maven dependencies
  security-scan       Run security vulnerability scan
  code-format         Format code using standard formatters
  
WILDFLY MANAGEMENT:
  wildfly-start       Start WildFly server
  wildfly-stop        Stop WildFly server
  wildfly-restart     Restart WildFly server
  wildfly-status      Show WildFly server status
  wildfly-logs        Tail WildFly logs
  wildfly-deploy      Deploy current WAR to WildFly
  wildfly-undeploy    Undeploy application from WildFly
  wildfly-redeploy    Redeploy application
  
DATABASE UTILITIES:
  db-status           Check database connection
  db-reset            Reset database schema
  db-backup           Backup database
  db-restore          Restore database from backup
  db-migrate          Run database migrations
  db-console          Open database console
  
DEVELOPMENT UTILITIES:
  dev-setup           Setup complete development environment
  gen-test-data       Generate test data
  run-integration     Run integration tests
  perf-test           Run performance tests
  api-test            Test API endpoints
  health-check        Check application health
  
PROJECT UTILITIES:
  project-info        Show project information
  env-check           Check development environment
  port-check          Check for port conflicts
  disk-usage          Show project disk usage
  cleanup             Clean temporary files and caches
  
HELP:
  help                Show this help message
  
Examples:
  $0 quick-build              # Quick build
  $0 wildfly-deploy           # Deploy to WildFly
  $0 api-test                 # Test API endpoints
  $0 db-reset                 # Reset database
  $0 cleanup                  # Clean project

================================================================================
EOF
}

# Quick build without tests
quick_build() {
    log_header "QUICK BUILD"
    cd "$SCRIPT_DIR"
    
    log_info "Running quick build (no tests)..."
    mvn clean compile package -DskipTests -q
    
    if [ -f "target/durvalcrm.war" ]; then
        local size=$(ls -lh target/durvalcrm.war | awk '{print $5}')
        log_success "Build completed - WAR size: $size"
    else
        log_error "Build failed - WAR file not found"
        return 1
    fi
}

# Full build with tests
full_build() {
    log_header "FULL BUILD"
    cd "$SCRIPT_DIR"
    
    log_info "Running full build with tests..."
    mvn clean compile test package
    
    if [ -f "target/durvalcrm.war" ]; then
        local size=$(ls -lh target/durvalcrm.war | awk '{print $5}')
        log_success "Full build completed - WAR size: $size"
        
        # Show test results
        if [ -d "target/surefire-reports" ]; then
            local test_files=$(find target/surefire-reports -name "*.xml" | wc -l)
            log_info "Test reports generated: $test_files files"
        fi
    else
        log_error "Build failed - WAR file not found"
        return 1
    fi
}

# Clean build
clean_build() {
    log_header "CLEAN BUILD"
    cd "$SCRIPT_DIR"
    
    log_info "Cleaning project..."
    mvn clean -q
    
    log_info "Removing generated sources..."
    rm -rf src/main/generated 2>/dev/null || true
    
    log_info "Running full clean build..."
    mvn compile package -DskipTests
    
    log_success "Clean build completed"
}

# Watch for changes and auto-rebuild
watch_build() {
    log_header "WATCH BUILD"
    
    if ! command -v inotifywait &> /dev/null; then
        log_error "inotifywait not found. Install inotify-tools package."
        return 1
    fi
    
    log_info "Watching for changes in src/ directory..."
    log_info "Press Ctrl+C to stop watching"
    
    while true; do
        inotifywait -r -e modify,create,delete src/ --format '%w%f %e' 2>/dev/null
        log_info "Change detected, rebuilding..."
        if quick_build; then
            log_success "Auto-rebuild completed at $(date)"
        else
            log_error "Auto-rebuild failed at $(date)"
        fi
        sleep 2
    done
}

# Check dependencies
check_deps() {
    log_header "DEPENDENCY CHECK"
    cd "$SCRIPT_DIR"
    
    log_info "Analyzing dependencies..."
    mvn dependency:analyze
    
    log_info "Dependency tree:"
    mvn dependency:tree -Dverbose | head -50
    
    log_info "Checking for dependency conflicts..."
    mvn dependency:tree -Dverbose | grep -i conflict || log_success "No conflicts found"
}

# Update dependencies
update_deps() {
    log_header "UPDATE DEPENDENCIES"
    cd "$SCRIPT_DIR"
    
    log_info "Checking for dependency updates..."
    mvn versions:display-dependency-updates
    
    log_info "Checking for plugin updates..."
    mvn versions:display-plugin-updates
    
    log_warning "Review updates carefully before applying"
    log_info "To update: mvn versions:use-latest-versions"
}

# Security scan
security_scan() {
    log_header "SECURITY SCAN"
    cd "$SCRIPT_DIR"
    
    log_info "Running OWASP dependency check..."
    mvn org.owasp:dependency-check-maven:check
    
    if [ -f "target/dependency-check-report.html" ]; then
        log_success "Security report generated: target/dependency-check-report.html"
    fi
}

# Format code
code_format() {
    log_header "CODE FORMATTING"
    cd "$SCRIPT_DIR"
    
    if command -v google-java-format &> /dev/null; then
        log_info "Formatting Java files with google-java-format..."
        find src/main/java -name "*.java" -exec google-java-format -i {} +
        log_success "Java files formatted"
    else
        log_warning "google-java-format not found, skipping Java formatting"
    fi
    
    # Format XML files if xmllint is available
    if command -v xmllint &> /dev/null; then
        log_info "Formatting XML files..."
        find . -name "*.xml" -not -path "./target/*" -exec xmllint --format {} -o {} \; 2>/dev/null || true
        log_success "XML files formatted"
    fi
}

# WildFly management functions
wildfly_start() {
    log_header "WILDFLY START"
    
    if pgrep -f "wildfly" > /dev/null; then
        log_warning "WildFly is already running"
        return 0
    fi
    
    if systemctl is-active --quiet wildfly 2>/dev/null; then
        log_info "Starting WildFly via systemd..."
        sudo systemctl start wildfly
    elif [ -f "$WILDFLY_HOME/bin/standalone.sh" ]; then
        log_info "Starting WildFly directly..."
        nohup "$WILDFLY_HOME/bin/standalone.sh" > /dev/null 2>&1 &
        sleep 5
    else
        log_error "WildFly not found at $WILDFLY_HOME"
        return 1
    fi
    
    log_success "WildFly starting..."
}

wildfly_stop() {
    log_header "WILDFLY STOP"
    
    if systemctl is-active --quiet wildfly 2>/dev/null; then
        log_info "Stopping WildFly via systemd..."
        sudo systemctl stop wildfly
    else
        log_info "Stopping WildFly process..."
        pkill -f "wildfly" || true
    fi
    
    log_success "WildFly stopped"
}

wildfly_restart() {
    log_header "WILDFLY RESTART"
    wildfly_stop
    sleep 3
    wildfly_start
}

wildfly_status() {
    log_header "WILDFLY STATUS"
    
    if systemctl is-active --quiet wildfly 2>/dev/null; then
        log_success "WildFly service is active"
        systemctl status wildfly --no-pager
    else
        if pgrep -f "wildfly" > /dev/null; then
            log_success "WildFly process is running"
            ps aux | grep wildfly | grep -v grep
        else
            log_warning "WildFly is not running"
        fi
    fi
    
    # Check ports
    log_info "Port status:"
    netstat -tuln | grep -E ':(8080|8443|9990)' || log_warning "WildFly ports not listening"
}

wildfly_logs() {
    log_header "WILDFLY LOGS"
    
    if systemctl is-active --quiet wildfly 2>/dev/null; then
        log_info "Following systemd logs (Ctrl+C to exit)..."
        journalctl -u wildfly -f
    elif [ -f "$WILDFLY_HOME/standalone/log/server.log" ]; then
        log_info "Following server logs (Ctrl+C to exit)..."
        tail -f "$WILDFLY_HOME/standalone/log/server.log"
    else
        log_error "No WildFly logs found"
    fi
}

wildfly_deploy() {
    log_header "WILDFLY DEPLOY"
    cd "$SCRIPT_DIR"
    
    if [ ! -f "target/durvalcrm.war" ]; then
        log_info "WAR file not found, building first..."
        quick_build
    fi
    
    if [ -f "target/durvalcrm.war" ]; then
        log_info "Deploying WAR file..."
        cp "target/durvalcrm.war" "$WILDFLY_HOME/standalone/deployments/"
        log_success "WAR file copied to deployments directory"
        
        # Wait for deployment
        log_info "Waiting for deployment..."
        sleep 10
        
        if [ -f "$WILDFLY_HOME/standalone/deployments/durvalcrm.war.deployed" ]; then
            log_success "Deployment successful"
        elif [ -f "$WILDFLY_HOME/standalone/deployments/durvalcrm.war.failed" ]; then
            log_error "Deployment failed - check WildFly logs"
        else
            log_warning "Deployment status unknown"
        fi
    else
        log_error "WAR file not found after build"
    fi
}

wildfly_undeploy() {
    log_header "WILDFLY UNDEPLOY"
    
    log_info "Removing deployment..."
    rm -f "$WILDFLY_HOME/standalone/deployments/durvalcrm.war"*
    
    log_success "Application undeployed"
}

wildfly_redeploy() {
    log_header "WILDFLY REDEPLOY"
    wildfly_undeploy
    sleep 2
    wildfly_deploy
}

# Database utilities
db_status() {
    log_header "DATABASE STATUS"
    
    log_info "Checking PostgreSQL connection..."
    
    if command -v pg_isready &> /dev/null; then
        if pg_isready -h "$DB_HOST" -p "$DB_PORT" -d "$DB_NAME" -U "$DB_USER"; then
            log_success "Database is ready"
        else
            log_error "Database is not ready"
            return 1
        fi
    else
        log_warning "pg_isready not found, trying psql..."
        if PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "SELECT 1;" > /dev/null 2>&1; then
            log_success "Database connection successful"
        else
            log_error "Database connection failed"
            return 1
        fi
    fi
    
    # Show table info
    log_info "Database tables:"
    PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "\\dt" 2>/dev/null || true
}

db_reset() {
    log_header "DATABASE RESET"
    
    log_warning "This will DROP ALL TABLES in $DB_NAME database!"
    read -p "Are you sure? (yes/no): " confirm
    
    if [ "$confirm" = "yes" ]; then
        log_info "Dropping all tables..."
        PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" << EOF
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO $DB_USER;
GRANT ALL ON SCHEMA public TO public;
EOF
        log_success "Database reset completed"
    else
        log_info "Database reset cancelled"
    fi
}

db_backup() {
    log_header "DATABASE BACKUP"
    
    local backup_file="backup-$(date +%Y%m%d-%H%M%S).sql"
    
    log_info "Creating database backup: $backup_file"
    PGPASSWORD="$DB_PASSWORD" pg_dump -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" > "$backup_file"
    
    if [ -f "$backup_file" ]; then
        local size=$(ls -lh "$backup_file" | awk '{print $5}')
        log_success "Backup created: $backup_file (Size: $size)"
    else
        log_error "Backup failed"
    fi
}

db_console() {
    log_header "DATABASE CONSOLE"
    
    log_info "Opening PostgreSQL console..."
    log_info "Type \\q to exit"
    
    PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME"
}

# Development utilities
dev_setup() {
    log_header "DEVELOPMENT SETUP"
    
    log_info "Setting up development environment..."
    
    # Create development directories
    mkdir -p logs tmp backups
    
    # Generate development configuration
    cat > "dev-config.properties" << EOF
# Development Configuration
db.host=$DB_HOST
db.port=$DB_PORT
db.name=$DB_NAME
db.user=$DB_USER
wildfly.host=$WILDFLY_HOST
wildfly.port=$WILDFLY_PORT
app.environment=development
app.debug=true
EOF
    
    log_success "Development configuration created: dev-config.properties"
    
    # Check environment
    env_check
}

gen_test_data() {
    log_header "GENERATE TEST DATA"
    
    log_info "Generating test data..."
    
    # Create SQL script for test data
    cat > "test-data.sql" << EOF
-- DurvalCRM Test Data
INSERT INTO associados (nome, cpf, email, telefone, ativo, data_cadastro) VALUES
('João Silva', '12345678901', 'joao@email.com', '11999999999', true, CURRENT_DATE),
('Maria Santos', '23456789012', 'maria@email.com', '11888888888', true, CURRENT_DATE),
('Pedro Oliveira', '34567890123', 'pedro@email.com', '11777777777', true, CURRENT_DATE),
('Ana Costa', '45678901234', 'ana@email.com', '11666666666', false, CURRENT_DATE);

INSERT INTO mensalidades (associado_id, valor, data_vencimento, status, data_criacao) VALUES
(1, 10.90, CURRENT_DATE + INTERVAL '30 days', 'PENDENTE', CURRENT_DATE),
(2, 10.90, CURRENT_DATE + INTERVAL '30 days', 'PENDENTE', CURRENT_DATE),
(3, 10.90, CURRENT_DATE - INTERVAL '10 days', 'VENCIDA', CURRENT_DATE - INTERVAL '40 days'),
(1, 10.90, CURRENT_DATE - INTERVAL '40 days', 'PAGA', CURRENT_DATE - INTERVAL '70 days');
EOF
    
    log_info "Executing test data script..."
    if db_status; then
        PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "test-data.sql"
        log_success "Test data generated successfully"
    else
        log_error "Cannot connect to database"
    fi
}

api_test() {
    log_header "API TESTING"
    
    local base_url="http://$WILDFLY_HOST:8080/durvalcrm/api"
    
    log_info "Testing API endpoints..."
    
    # Test endpoints
    local endpoints=(
        "/health"
        "/associados"
        "/mensalidades"
        "/dashboard"
    )
    
    for endpoint in "${endpoints[@]}"; do
        local url="$base_url$endpoint"
        log_info "Testing: $endpoint"
        
        local response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$url" 2>/dev/null)
        local status=$(echo "$response" | grep -o "HTTPSTATUS:[0-9]*" | cut -d: -f2)
        
        case $status in
            200) log_success "✓ $endpoint - OK" ;;
            401|403) log_warning "⚠ $endpoint - Authentication required" ;;
            404) log_error "✗ $endpoint - Not found" ;;
            500) log_error "✗ $endpoint - Server error" ;;
            *) log_warning "? $endpoint - Status: $status" ;;
        esac
    done
}

health_check() {
    log_header "HEALTH CHECK"
    
    local health_url="http://$WILDFLY_HOST:8080/durvalcrm/api/health"
    
    log_info "Checking application health..."
    
    local response=$(curl -s "$health_url" 2>/dev/null)
    if [ $? -eq 0 ]; then
        log_success "Health endpoint is responding"
        echo "$response" | jq . 2>/dev/null || echo "$response"
    else
        log_error "Health endpoint is not responding"
        return 1
    fi
}

# Project utilities
project_info() {
    log_header "PROJECT INFORMATION"
    cd "$SCRIPT_DIR"
    
    log_info "Project: DurvalCRM J2EE"
    log_info "Directory: $(pwd)"
    
    if [ -f "pom.xml" ]; then
        local version=$(grep -m1 "<version>" pom.xml | sed 's/.*<version>//;s/<\/version>.*//')
        local artifactId=$(grep -m1 "<artifactId>" pom.xml | sed 's/.*<artifactId>//;s/<\/artifactId>.*//')
        log_info "Artifact: $artifactId"
        log_info "Version: $version"
    fi
    
    # Git info if available
    if [ -d ".git" ]; then
        local branch=$(git branch --show-current 2>/dev/null || echo "unknown")
        local commit=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
        log_info "Git branch: $branch"
        log_info "Git commit: $commit"
    fi
    
    # File counts
    local java_files=$(find src -name "*.java" 2>/dev/null | wc -l)
    local xml_files=$(find . -name "*.xml" -not -path "./target/*" 2>/dev/null | wc -l)
    log_info "Java files: $java_files"
    log_info "XML files: $xml_files"
    
    # Build info
    if [ -f "target/durvalcrm.war" ]; then
        local war_size=$(ls -lh target/durvalcrm.war | awk '{print $5}')
        local war_date=$(ls -l target/durvalcrm.war | awk '{print $6, $7, $8}')
        log_info "WAR file: $war_size (built: $war_date)"
    else
        log_warning "WAR file not found - run build first"
    fi
}

env_check() {
    log_header "ENVIRONMENT CHECK"
    
    # Check Java
    if command -v java &> /dev/null; then
        local java_version=$(java -version 2>&1 | head -n 1)
        log_success "Java: $java_version"
    else
        log_error "Java not found"
    fi
    
    # Check Maven
    if command -v mvn &> /dev/null; then
        local mvn_version=$(mvn -version | head -n 1)
        log_success "Maven: $mvn_version"
    else
        log_error "Maven not found"
    fi
    
    # Check WildFly
    if [ -d "$WILDFLY_HOME" ]; then
        log_success "WildFly: $WILDFLY_HOME"
    else
        log_warning "WildFly not found at: $WILDFLY_HOME"
    fi
    
    # Check PostgreSQL client
    if command -v psql &> /dev/null; then
        local psql_version=$(psql --version)
        log_success "PostgreSQL client: $psql_version"
    else
        log_warning "PostgreSQL client not found"
    fi
    
    # Check database connection
    if db_status &> /dev/null; then
        log_success "Database connection: OK"
    else
        log_warning "Database connection: Failed"
    fi
}

port_check() {
    log_header "PORT CHECK"
    
    local ports=(8080 8443 9990 5432)
    
    for port in "${ports[@]}"; do
        if netstat -tuln | grep -q ":$port "; then
            log_warning "Port $port is in use"
            netstat -tuln | grep ":$port "
        else
            log_success "Port $port is available"
        fi
    done
}

disk_usage() {
    log_header "DISK USAGE"
    cd "$SCRIPT_DIR"
    
    log_info "Project directory usage:"
    du -sh . 2>/dev/null || log_error "Cannot calculate disk usage"
    
    log_info "Subdirectory breakdown:"
    du -sh */ 2>/dev/null | sort -hr || true
    
    if [ -d "target" ]; then
        log_info "Build directory (target/):"
        du -sh target/* 2>/dev/null | sort -hr || true
    fi
}

cleanup() {
    log_header "PROJECT CLEANUP"
    cd "$SCRIPT_DIR"
    
    log_info "Cleaning Maven build artifacts..."
    mvn clean -q 2>/dev/null || true
    
    log_info "Removing temporary files..."
    find . -name "*.tmp" -delete 2>/dev/null || true
    find . -name "*.log" -not -path "./logs/*" -delete 2>/dev/null || true
    find . -name "*.cache" -delete 2>/dev/null || true
    find . -name ".DS_Store" -delete 2>/dev/null || true
    
    log_info "Cleaning IDE files..."
    rm -rf .idea/ .vscode/ *.iml 2>/dev/null || true
    
    log_info "Cleaning backup files..."
    find . -name "*~" -delete 2>/dev/null || true
    find . -name "*.bak" -delete 2>/dev/null || true
    
    log_success "Cleanup completed"
}

# Main function to handle commands
main() {
    local command="${1:-help}"
    
    case "$command" in
        # Build commands
        "quick-build") quick_build ;;
        "full-build") full_build ;;
        "clean-build") clean_build ;;
        "watch-build") watch_build ;;
        
        # Build utilities
        "check-deps") check_deps ;;
        "update-deps") update_deps ;;
        "security-scan") security_scan ;;
        "code-format") code_format ;;
        
        # WildFly management
        "wildfly-start") wildfly_start ;;
        "wildfly-stop") wildfly_stop ;;
        "wildfly-restart") wildfly_restart ;;
        "wildfly-status") wildfly_status ;;
        "wildfly-logs") wildfly_logs ;;
        "wildfly-deploy") wildfly_deploy ;;
        "wildfly-undeploy") wildfly_undeploy ;;
        "wildfly-redeploy") wildfly_redeploy ;;
        
        # Database utilities
        "db-status") db_status ;;
        "db-reset") db_reset ;;
        "db-backup") db_backup ;;
        "db-console") db_console ;;
        
        # Development utilities
        "dev-setup") dev_setup ;;
        "gen-test-data") gen_test_data ;;
        "api-test") api_test ;;
        "health-check") health_check ;;
        
        # Project utilities
        "project-info") project_info ;;
        "env-check") env_check ;;
        "port-check") port_check ;;
        "disk-usage") disk_usage ;;
        "cleanup") cleanup ;;
        
        # Help
        "help"|"-h"|"--help") show_help ;;
        
        *)
            log_error "Unknown command: $command"
            echo "Use '$0 help' to see available commands"
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"