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

# Change to application directory
cd $APP_DIR

# Remove old JAR if exists
sudo rm -f *.jar

# Build the application
echo "Building application..."
mvn clean package -DskipTests

# Copy the JAR to the app directory
sudo cp target/$JAR_NAME $APP_DIR/

# Set permissions
sudo chown -R $USER:$USER $APP_DIR
sudo chmod +x $APP_DIR/$JAR_NAME

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
sudo systemctl start $SERVICE_NAME

echo "Deployment completed successfully!"
echo "Service status:"
sudo systemctl status $SERVICE_NAME --no-pager

echo "Checking application logs (last 20 lines):"
sleep 5
sudo tail -n 20 $LOG_DIR/app.log || echo "Log file not yet available"

echo "Application should be available at: http://localhost:8080"
echo "Health check: http://localhost:8080/actuator/health" 