#!/bin/bash

# Deployment script for Spring Boot application
# This script builds and deploys the application without Docker

set -e  # Exit on any error

APP_NAME="hackathon-backend"
APP_DIR="/opt/$APP_NAME"
SERVICE_NAME="$APP_NAME"
JAR_NAME="app-0.0.1-SNAPSHOT.jar"
LOG_DIR="/var/log/$APP_NAME"

echo "Starting deployment of $APP_NAME..."

# Create application directory if it doesn't exist
sudo mkdir -p $APP_DIR
sudo mkdir -p $LOG_DIR

# Stop existing service if running
echo "Stopping existing service..."
sudo systemctl stop $SERVICE_NAME || true

# Install Java 21 if not installed
if ! java -version 2>&1 | grep -q "21."; then
    echo "Installing Java 21..."
    sudo apt update
    sudo apt install -y openjdk-21-jdk
fi

# Install Maven if not installed
if ! command -v mvn &> /dev/null; then
    echo "Installing Maven..."
    sudo apt update
    sudo apt install -y maven
fi

# Ensure we're in the correct directory
echo "Working directory: $(pwd)"
if [ ! -f "pom.xml" ]; then
    echo "Error: pom.xml not found. Make sure you're in the project root directory."
    echo "Contents of current directory:"
    ls -la
    exit 1
fi

# Remove old JAR if exists
echo "Cleaning old JAR files..."
sudo rm -f $APP_DIR/*.jar

# Build the application
echo "Building application..."
mvn clean package -DskipTests

# Check if JAR was created
if [ ! -f "target/$JAR_NAME" ]; then
    echo "Error: JAR file not found at target/$JAR_NAME"
    echo "Contents of target directory:"
    ls -la target/ || echo "Target directory not found"
    exit 1
fi

# Copy the JAR to the app directory
echo "Copying JAR to application directory..."
sudo cp target/$JAR_NAME $APP_DIR/

# Set permissions
sudo chown -R $USER:$USER $APP_DIR
sudo chmod +x $APP_DIR/$JAR_NAME

echo "JAR file deployed: $APP_DIR/$JAR_NAME"
echo "JAR file size: $(ls -lh $APP_DIR/$JAR_NAME | awk '{print $5}')"

# Create systemd service file
echo "Creating systemd service..."
sudo tee /etc/systemd/system/$SERVICE_NAME.service > /dev/null <<EOF
[Unit]
Description=Hackathon FII Practic Backend
After=network.target
Wants=network-online.target

[Service]
Type=exec
User=$USER
Group=$USER
WorkingDirectory=$APP_DIR
ExecStart=/usr/bin/java -jar $APP_DIR/$JAR_NAME
ExecStop=/bin/kill -TERM \$MAINPID
Restart=always
RestartSec=10
StandardOutput=append:$LOG_DIR/app.log
StandardError=append:$LOG_DIR/error.log

# Environment variables for production
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=DATABASE_URL=jdbc:postgresql://octavianregatun.com:5432/hackathon-fiipractic
Environment=DATABASE_USERNAME=postgres
Environment=DATABASE_PASSWORD=9234
Environment=JWT_SECRET=7yqwYBqYu8b1bedddyeszUCwIaB962Q/5W2Bwv3RQ7I=
Environment=JWT_EXPIRATION=86400000
Environment=GOOGLE_CLIENT_ID=476589245232-je77eht00s9a8ild559f4omu6v1j3ik6.apps.googleusercontent.com
Environment=GOOGLE_CLIENT_SECRET=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
Environment=FRONTEND_URL=https://hackathon-fiipractic.octavianregatun.com
Environment=DDL_AUTO=update
Environment=SHOW_SQL=false

# JVM options
Environment=JAVA_OPTS=-Xmx512m -Xms256m

[Install]
WantedBy=multi-user.target
EOF

# Reload systemd daemon
sudo systemctl daemon-reload

# Enable and start the service
sudo systemctl enable $SERVICE_NAME
echo "Starting $SERVICE_NAME service..."
sudo systemctl start $SERVICE_NAME

# Wait a moment for the service to start
sleep 3

echo "Deployment completed successfully!"
echo ""
echo "=== Service Status ==="
sudo systemctl status $SERVICE_NAME --no-pager || true

echo ""
echo "=== Checking if application is responding ==="
for i in {1..6}; do
    echo "Attempt $i/6: Checking application startup..."
    if curl -f -s http://localhost:8080/actuator/health >/dev/null 2>&1; then
        echo "✅ Application is responding!"
        break
    else
        if [ $i -eq 6 ]; then
            echo "⚠️  Application may still be starting up..."
        else
            sleep 5
        fi
    fi
done

echo ""
echo "=== Recent Application Logs ==="
sudo tail -n 15 $LOG_DIR/app.log 2>/dev/null || echo "Log file not yet available"

echo ""
echo "Application endpoints:"
echo "- Health check: http://localhost:8080/actuator/health"
echo "- API Documentation: http://localhost:8080/swagger-ui"
echo "- Application logs: $LOG_DIR/app.log"
echo ""
echo "Use './scripts/manage.sh status' to check service status"
echo "Use './scripts/manage.sh logs' to view recent logs" 