#!/bin/bash

# =============================================================================
# DurvalCRM J2EE Build Script
# =============================================================================
# Description: Comprehensive build script for DurvalCRM J2EE application
# Target: WildFly 37 deployment
# Author: Build automation script
# Version: 1.0.0
# =============================================================================

set -e  # Exit on any error

# Script configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="DurvalCRM J2EE"
WAR_NAME="durvalcrm.war"
BUILD_PROFILE="${BUILD_PROFILE:-dev}"
SKIP_TESTS="${SKIP_TESTS:-false}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

# Print banner
print_banner() {
    echo "============================================================================="
    echo "                        $PROJECT_NAME Build Script"
    echo "============================================================================="
    echo "Build Profile: $BUILD_PROFILE"
    echo "Skip Tests: $SKIP_TESTS"
    echo "Project Dir: $SCRIPT_DIR"
    echo "============================================================================="
    echo
}

# Check prerequisites
check_prerequisites() {
    log_info "Checking prerequisites..."
    
    # Check Java version
    if ! command -v java &> /dev/null; then
        log_error "Java is not installed or not in PATH"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | grep "version" | awk '{print $3}' | tr -d '"' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        log_error "Java 17 or higher is required. Current version: $JAVA_VERSION"
        exit 1
    fi
    log_success "Java version: $(java -version 2>&1 | head -n 1)"
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        log_error "Maven is not installed or not in PATH"
        exit 1
    fi
    log_success "Maven version: $(mvn -version | head -n 1)"
    
    echo
}

# Clean previous builds
clean_build() {
    log_info "Cleaning previous builds..."
    
    cd "$SCRIPT_DIR"
    
    if [ -d "target" ]; then
        rm -rf target
        log_success "Removed target directory"
    fi
    
    # Clean any generated sources
    if [ -d "src/main/generated" ]; then
        rm -rf src/main/generated
        log_success "Removed generated sources"
    fi
    
    mvn clean -q
    log_success "Maven clean completed"
    echo
}

# Validate project structure
validate_project() {
    log_info "Validating project structure..."
    
    cd "$SCRIPT_DIR"
    
    # Check essential files
    local essential_files=(
        "pom.xml"
        "src/main/java"
        "src/main/webapp/WEB-INF/web.xml"
        "src/main/webapp/WEB-INF/beans.xml"
        "src/main/webapp/WEB-INF/jboss-web.xml"
        "src/main/resources/META-INF/persistence.xml"
    )
    
    for file in "${essential_files[@]}"; do
        if [ ! -e "$file" ]; then
            log_error "Essential file missing: $file"
            exit 1
        fi
    done
    
    log_success "All essential files present"
    
    # Validate pom.xml
    mvn validate -q
    log_success "POM validation passed"
    echo
}

# Compile source code
compile_sources() {
    log_info "Compiling source code..."
    
    cd "$SCRIPT_DIR"
    
    # Set Maven options
    local maven_opts="-Dmaven.compiler.source=17 -Dmaven.compiler.target=17"
    
    if [ "$BUILD_PROFILE" = "prod" ]; then
        maven_opts="$maven_opts -Pprod"
    else
        maven_opts="$maven_opts -Pdev"
    fi
    
    # Compile
    mvn compile $maven_opts
    log_success "Source compilation completed"
    
    # Check for generated MapStruct implementations
    if [ -d "target/generated-sources/annotations" ]; then
        local mapstruct_files=$(find target/generated-sources/annotations -name "*Impl.java" 2>/dev/null | wc -l)
        log_success "Generated $mapstruct_files MapStruct implementation files"
    fi
    
    echo
}

# Run tests
run_tests() {
    if [ "$SKIP_TESTS" = "true" ]; then
        log_warning "Skipping tests as requested"
        return 0
    fi
    
    log_info "Running tests..."
    
    cd "$SCRIPT_DIR"
    
    # Run unit tests
    mvn test -q
    
    # Check test results
    if [ -d "target/surefire-reports" ]; then
        local test_files=$(find target/surefire-reports -name "*.xml" 2>/dev/null | wc -l)
        log_success "Completed $test_files test suites"
        
        # Check for test failures
        if grep -r "failures=\"[^0]" target/surefire-reports/*.xml 2>/dev/null; then
            log_error "Some tests failed"
            exit 1
        fi
        
        if grep -r "errors=\"[^0]" target/surefire-reports/*.xml 2>/dev/null; then
            log_error "Some tests had errors"
            exit 1
        fi
    fi
    
    log_success "All tests passed"
    echo
}

# Package WAR file
package_war() {
    log_info "Packaging WAR file..."
    
    cd "$SCRIPT_DIR"
    
    # Set Maven options
    local maven_opts=""
    if [ "$BUILD_PROFILE" = "prod" ]; then
        maven_opts="-Pprod"
    else
        maven_opts="-Pdev"
    fi
    
    if [ "$SKIP_TESTS" = "true" ]; then
        maven_opts="$maven_opts -DskipTests"
    fi
    
    # Package
    mvn package $maven_opts
    
    # Verify WAR file
    if [ ! -f "target/$WAR_NAME" ]; then
        log_error "WAR file not generated: target/$WAR_NAME"
        exit 1
    fi
    
    local war_size=$(ls -lh "target/$WAR_NAME" | awk '{print $5}')
    log_success "WAR file generated: target/$WAR_NAME (Size: $war_size)"
    
    # Verify WAR contents
    log_info "Verifying WAR contents..."
    local war_contents=$(jar -tf "target/$WAR_NAME" | wc -l)
    log_success "WAR contains $war_contents files"
    
    # Check for essential WAR components
    local essential_war_contents=(
        "WEB-INF/web.xml"
        "WEB-INF/beans.xml" 
        "WEB-INF/jboss-web.xml"
        "WEB-INF/classes/META-INF/persistence.xml"
        "WEB-INF/classes/br/org/cecairbar/durvalcrm/"
    )
    
    for content in "${essential_war_contents[@]}"; do
        if ! jar -tf "target/$WAR_NAME" | grep -q "$content"; then
            log_error "Essential WAR content missing: $content"
            exit 1
        fi
    done
    
    log_success "WAR file validation passed"
    echo
}

# Generate build report
generate_report() {
    log_info "Generating build report..."
    
    cd "$SCRIPT_DIR"
    
    local report_file="target/build-report.txt"
    
    cat > "$report_file" << EOF
================================================================================
                          DurvalCRM J2EE Build Report
================================================================================
Build Date: $(date)
Build Profile: $BUILD_PROFILE
Skip Tests: $SKIP_TESTS
Java Version: $(java -version 2>&1 | head -n 1)
Maven Version: $(mvn -version | head -n 1)

================================================================================
                                Build Artifacts
================================================================================
WAR File: target/$WAR_NAME
WAR Size: $(ls -lh "target/$WAR_NAME" | awk '{print $5}')
WAR Contents: $(jar -tf "target/$WAR_NAME" | wc -l) files

================================================================================
                              Project Dependencies
================================================================================
$(mvn dependency:tree -q | head -20)

================================================================================
                                Test Results
================================================================================
EOF

    if [ "$SKIP_TESTS" = "false" ] && [ -d "target/surefire-reports" ]; then
        echo "Test Results:" >> "$report_file"
        find target/surefire-reports -name "*.xml" -exec grep -l "testsuite" {} \; | while read file; do
            local tests=$(grep -o 'tests="[0-9]*"' "$file" | cut -d'"' -f2)
            local failures=$(grep -o 'failures="[0-9]*"' "$file" | cut -d'"' -f2)
            local errors=$(grep -o 'errors="[0-9]*"' "$file" | cut -d'"' -f2)
            local name=$(grep -o 'name="[^"]*"' "$file" | cut -d'"' -f2)
            echo "  $name: $tests tests, $failures failures, $errors errors" >> "$report_file"
        done
    else
        echo "Tests were skipped" >> "$report_file"
    fi
    
    cat >> "$report_file" << EOF

================================================================================
                            Deployment Information
================================================================================
Application Name: DurvalCRM
Context Root: /durvalcrm
WAR File: $WAR_NAME
Target Server: WildFly 37
Database: PostgreSQL
DataSource JNDI: java:jboss/datasources/DurvalCRMDS

API Endpoints:
  - Health: /durvalcrm/api/health
  - Members: /durvalcrm/api/associados
  - Payments: /durvalcrm/api/mensalidades
  - Donations: /durvalcrm/api/doacoes
  - Sales: /durvalcrm/api/vendas
  - Dashboard: /durvalcrm/api/dashboard

================================================================================
                                Build Status
================================================================================
Status: SUCCESS
Build Duration: $((SECONDS))s
Timestamp: $(date)
================================================================================
EOF

    log_success "Build report generated: $report_file"
    echo
}

# Display build summary
display_summary() {
    echo "============================================================================="
    echo "                           BUILD COMPLETED SUCCESSFULLY"
    echo "============================================================================="
    echo
    log_success "WAR file ready for deployment:"
    echo "  File: $(pwd)/target/$WAR_NAME"
    echo "  Size: $(ls -lh "target/$WAR_NAME" | awk '{print $5}')"
    echo
    log_info "Next steps:"
    echo "  1. Deploy WAR to WildFly: cp target/$WAR_NAME \$WILDFLY_HOME/standalone/deployments/"
    echo "  2. Configure PostgreSQL DataSource in WildFly"
    echo "  3. Start/restart WildFly server"
    echo "  4. Access application at: http://your-server:8080/durvalcrm/"
    echo
    log_info "For automated deployment, run: ./deploy.sh"
    echo "============================================================================="
}

# Main execution
main() {
    # Record start time
    local start_time=$(date +%s)
    
    print_banner
    check_prerequisites
    validate_project
    clean_build
    compile_sources
    run_tests
    package_war
    generate_report
    
    # Calculate build duration
    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    SECONDS=$duration
    
    display_summary
    
    log_success "Build completed in ${duration}s"
}

# Handle script arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --profile=*)
            BUILD_PROFILE="${1#*=}"
            shift
            ;;
        --skip-tests)
            SKIP_TESTS="true"
            shift
            ;;
        --clean-only)
            clean_build
            exit 0
            ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS]"
            echo "Options:"
            echo "  --profile=PROFILE    Build profile (dev|prod) [default: dev]"
            echo "  --skip-tests         Skip running tests"
            echo "  --clean-only         Only clean, don't build"
            echo "  --help, -h           Show this help message"
            exit 0
            ;;
        *)
            log_error "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Run main function
main