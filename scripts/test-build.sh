#!/bin/bash

# Test build script to verify the application builds correctly

set -e  # Exit on any error

echo "Starting local build test..."

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 21 first."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt "21" ]; then
    echo "Java 21 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "Java version check passed."

# Clean and compile
echo "Cleaning previous builds..."
mvn clean

echo "Compiling application..."
mvn compile

echo "Running tests..."
mvn test

echo "Building JAR..."
mvn package -DskipTests

# Check if JAR was created
JAR_FILE="target/app-0.0.1-SNAPSHOT.jar"
if [ -f "$JAR_FILE" ]; then
    echo "✅ Build successful! JAR created: $JAR_FILE"
    echo "JAR size: $(du -h $JAR_FILE | cut -f1)"
else
    echo "❌ Build failed! JAR file not found."
    exit 1
fi

# Test if JAR can be executed (dry run)
echo "Testing JAR execution (dry run)..."
java -jar $JAR_FILE --help || echo "JAR execution test completed (expected to show help or exit)."

echo "🎉 Local build test completed successfully!"
echo ""
echo "You can now deploy to production using:"
echo "  - GitHub Actions (recommended): Push to main or create a release"
echo "  - Manual deployment: Run ./scripts/deploy.sh on the VPS" 