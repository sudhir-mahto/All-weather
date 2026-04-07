#!/usr/bin/env bash
# =============================================================================
# setup-db.sh - All Weather Solution MySQL Database Setup via Podman
# =============================================================================
# This script creates and starts a MySQL 8.0 container using Podman,
# sets up the database and user for the All Weather Solution backend.
#
# Requirements:
#   - Podman installed (brew install podman on macOS, or dnf/apt on Linux)
#   - Podman machine running on macOS: podman machine start
#
# Usage:
#   chmod +x setup-db.sh
#   ./setup-db.sh
# =============================================================================

set -e  # Exit immediately on any error

# ── Configuration ──────────────────────────────────────────────────────────────
CONTAINER_NAME="allweather-mysql"
MYSQL_IMAGE="mysql:8.0"
MYSQL_ROOT_PASSWORD="RootAllWeather@2024"
MYSQL_DATABASE="allweather_db"
MYSQL_USER="allweather_user"
MYSQL_PASSWORD="AllWeather@2024"
HOST_PORT="3306"
CONTAINER_PORT="3306"

# ── Color Output ───────────────────────────────────────────────────────────────
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=============================================="
echo " All Weather Solution - Database Setup"
echo -e "==============================================${NC}"
echo ""

# ── Step 1: Check if Podman is installed ──────────────────────────────────────
echo -e "${YELLOW}[1/6] Checking Podman installation...${NC}"
if ! command -v podman &> /dev/null; then
    echo -e "${RED}ERROR: Podman is not installed.${NC}"
    echo "Install it:"
    echo "  macOS:  brew install podman && podman machine init && podman machine start"
    echo "  Fedora: sudo dnf install podman"
    echo "  Ubuntu: sudo apt install podman"
    exit 1
fi
echo -e "${GREEN}Podman found: $(podman --version)${NC}"
echo ""

# ── Step 2: Remove existing container if it exists ───────────────────────────
echo -e "${YELLOW}[2/6] Checking for existing container...${NC}"
if podman ps -a --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
    echo "Found existing container '${CONTAINER_NAME}'. Removing it..."
    podman stop "${CONTAINER_NAME}" 2>/dev/null || true
    podman rm "${CONTAINER_NAME}" 2>/dev/null || true
    echo "Old container removed."
else
    echo "No existing container found."
fi
echo ""

# ── Step 3: Pull MySQL 8.0 image ──────────────────────────────────────────────
echo -e "${YELLOW}[3/6] Pulling MySQL 8.0 image...${NC}"
podman pull "${MYSQL_IMAGE}"
echo -e "${GREEN}MySQL image ready.${NC}"
echo ""

# ── Step 4: Create and start the MySQL container ──────────────────────────────
echo -e "${YELLOW}[4/6] Starting MySQL container...${NC}"
podman run -d \
    --name "${CONTAINER_NAME}" \
    -e MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}" \
    -e MYSQL_DATABASE="${MYSQL_DATABASE}" \
    -e MYSQL_USER="${MYSQL_USER}" \
    -e MYSQL_PASSWORD="${MYSQL_PASSWORD}" \
    -p "${HOST_PORT}:${CONTAINER_PORT}" \
    --restart unless-stopped \
    "${MYSQL_IMAGE}"

echo -e "${GREEN}Container '${CONTAINER_NAME}' started.${NC}"
echo ""

# ── Step 5: Wait for MySQL to be ready ───────────────────────────────────────
echo -e "${YELLOW}[5/6] Waiting for MySQL to be ready...${NC}"
echo "This may take 20-40 seconds on first start..."

MAX_RETRIES=30
RETRY_INTERVAL=3
RETRIES=0

while [ $RETRIES -lt $MAX_RETRIES ]; do
    # Try to connect and run a simple query
    if podman exec "${CONTAINER_NAME}" mysqladmin ping \
        -u root \
        --password="${MYSQL_ROOT_PASSWORD}" \
        --silent 2>/dev/null; then
        echo -e "${GREEN}MySQL is ready!${NC}"
        break
    fi

    RETRIES=$((RETRIES + 1))
    echo "  Attempt ${RETRIES}/${MAX_RETRIES} - MySQL not ready yet, waiting ${RETRY_INTERVAL}s..."
    sleep $RETRY_INTERVAL
done

if [ $RETRIES -eq $MAX_RETRIES ]; then
    echo -e "${RED}ERROR: MySQL did not become ready in time.${NC}"
    echo "Check container logs: podman logs ${CONTAINER_NAME}"
    exit 1
fi
echo ""

# ── Step 6: Grant privileges and verify setup ─────────────────────────────────
echo -e "${YELLOW}[6/6] Configuring database privileges...${NC}"

podman exec "${CONTAINER_NAME}" mysql \
    -u root \
    --password="${MYSQL_ROOT_PASSWORD}" \
    -e "
    -- Ensure the database exists
    CREATE DATABASE IF NOT EXISTS \`${MYSQL_DATABASE}\`
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

    -- Ensure user exists and grant full access to our database
    CREATE USER IF NOT EXISTS '${MYSQL_USER}'@'%'
        IDENTIFIED BY '${MYSQL_PASSWORD}';

    GRANT ALL PRIVILEGES ON \`${MYSQL_DATABASE}\`.* TO '${MYSQL_USER}'@'%';

    FLUSH PRIVILEGES;

    -- Confirm setup
    SELECT 'Database setup complete!' AS status;
    SHOW DATABASES;
    "

echo ""
echo -e "${GREEN}=============================================="
echo " Setup Complete!"
echo -e "==============================================${NC}"
echo ""
echo "  Container Name : ${CONTAINER_NAME}"
echo "  MySQL Host     : localhost"
echo "  MySQL Port     : ${HOST_PORT}"
echo "  Database       : ${MYSQL_DATABASE}"
echo "  Username       : ${MYSQL_USER}"
echo "  Password       : ${MYSQL_PASSWORD}"
echo ""
echo "  JDBC URL: jdbc:mysql://localhost:${HOST_PORT}/${MYSQL_DATABASE}"
echo ""
echo -e "${YELLOW}Useful commands:${NC}"
echo "  View logs      : podman logs -f ${CONTAINER_NAME}"
echo "  Stop container : podman stop ${CONTAINER_NAME}"
echo "  Start container: podman start ${CONTAINER_NAME}"
echo "  MySQL shell    : podman exec -it ${CONTAINER_NAME} mysql -u ${MYSQL_USER} -p ${MYSQL_DATABASE}"
echo ""
echo -e "${GREEN}Now start the Spring Boot backend:${NC}"
echo "  cd backend && mvn spring-boot:run"
echo ""
