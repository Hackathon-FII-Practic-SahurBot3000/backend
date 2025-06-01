#!/bin/bash

# Quick start script for testing the Fair Voting System

echo "🎯 Fair Voting System - Quick Start"
echo "=================================="

# Make test script executable
chmod +x test-fair-voting.sh

echo "1. Starting test menu..."
echo "   You can also test manually with these quick commands:"
echo ""
echo "   Quick Demo (8 teams):"
echo "   curl -X GET 'http://localhost:8080/api/test/quick-demo/8'"
echo ""
echo "   Create test data with 6 teams:"
echo "   curl -X POST 'http://localhost:8080/api/test/seed-data/6'"
echo ""
echo "   Clean up test data:"
echo "   curl -X DELETE 'http://localhost:8080/api/test/cleanup'"
echo ""
echo "Press Enter to start the interactive test menu..."
read

# Start the test script
./test-fair-voting.sh 