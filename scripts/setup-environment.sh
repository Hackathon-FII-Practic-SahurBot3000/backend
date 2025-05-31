#!/bin/bash

# Environment setup script for VPS
# This script installs all necessary dependencies for running the Spring Boot application

set -e  # Exit on any error

echo "Setting up environment for Spring Boot application..."

# Update package manager
echo "Updating package manager..."
sudo apt update

# Install Java 21
if ! java -version 2>&1 | grep -q "21."; then
    echo "Installing Java 21..."
    sudo apt install -y openjdk-21-jdk
else
    echo "Java 21 is already installed."
fi

# Install Maven
if ! command -v mvn &> /dev/null; then
    echo "Installing Maven..."
    sudo apt install -y maven
else
    echo "Maven is already installed."
fi

# Install Git
if ! command -v git &> /dev/null; then
    echo "Installing Git..."
    sudo apt install -y git
else
    echo "Git is already installed."
fi

# Install curl for health checks
if ! command -v curl &> /dev/null; then
    echo "Installing curl..."
    sudo apt install -y curl
else
    echo "curl is already installed."
fi

# Install jq for JSON parsing (optional, for better health check output)
if ! command -v jq &> /dev/null; then
    echo "Installing jq for JSON parsing..."
    sudo apt install -y jq
else
    echo "jq is already installed."
fi

# Verify installations
echo ""
echo "=== Environment Setup Complete ==="
echo "Java version:"
java -version

echo ""
echo "Maven version:"
mvn -version

echo ""
echo "Git version:"
git --version

echo ""
echo "All dependencies are installed successfully!"
echo "You can now proceed with application deployment." 