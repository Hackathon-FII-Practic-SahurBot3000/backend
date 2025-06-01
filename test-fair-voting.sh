#!/bin/bash

# Fair Voting System Test Script
# This script provides an easy way to test the fair voting system

BASE_URL="http://localhost:8080"
BOLD='\033[1m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BOLD}🎯 Fair Voting System Test Script${NC}"
echo "=========================================="

# Check if server is running
echo -e "${YELLOW}🔍 Checking if server is running...${NC}"
if ! curl -s "$BASE_URL/api/test/health" > /dev/null 2>&1; then
    echo -e "${RED}❌ Server is not running or test API is not accessible.${NC}"
    echo "Please start your Spring Boot application first."
    echo "Run: ./mvnw spring-boot:run"
    exit 1
fi
echo -e "${GREEN}✅ Server is running and test API is accessible${NC}"

# Function to test with different team counts
test_team_count() {
    local team_count=$1
    echo -e "\n${BOLD}📊 Testing with $team_count teams...${NC}"
    
    response=$(curl -s -X GET "$BASE_URL/api/test/quick-demo/$team_count")
    
    if echo "$response" | grep -q '"error"'; then
        echo -e "${RED}❌ Error: $(echo "$response" | jq -r '.error')${NC}"
        return 1
    fi
    
    is_fair=$(echo "$response" | jq -r '.results.isFair')
    total_clusters=$(echo "$response" | jq -r '.results.totalClusters')
    total_rounds=$(echo "$response" | jq -r '.results.totalRounds')
    message=$(echo "$response" | jq -r '.results.fairnessMessage')
    
    if [ "$is_fair" = "true" ]; then
        echo -e "${GREEN}✅ Fair distribution achieved${NC}"
    else
        echo -e "${YELLOW}⚠️  Distribution needs improvement${NC}"
    fi
    
    echo "   📈 Total clusters: $total_clusters"
    echo "   🔄 Total rounds: $total_rounds"
    echo "   💬 Message: $message"
}

# Main menu
show_menu() {
    echo -e "\n${BOLD}🎮 Select a test option:${NC}"
    echo "1. Quick Demo (8 teams)"
    echo "2. Test Multiple Scenarios"
    echo "3. Test Edge Cases"
    echo "4. Custom Team Count"
    echo "5. Performance Test (4-20 teams)"
    echo "6. Clean Up Test Data"
    echo "7. View Test Data Summary"
    echo "8. Run Unit Tests"
    echo "9. Exit"
    echo -n "Enter your choice [1-9]: "
}

# Quick demo with 8 teams
quick_demo() {
    echo -e "\n${BOLD}🚀 Running Quick Demo${NC}"
    test_team_count 8
}

# Test multiple scenarios
multiple_scenarios() {
    echo -e "\n${BOLD}🧪 Testing Multiple Scenarios${NC}"
    
    echo -e "\n${YELLOW}Creating multiple test scenarios...${NC}"
    response=$(curl -s -X POST "$BASE_URL/api/test/seed-multiple-scenarios")
    echo "$response" | jq '.'
}

# Test edge cases
edge_cases() {
    echo -e "\n${BOLD}🐛 Testing Edge Cases${NC}"
    
    echo -e "\n${YELLOW}Running automated edge case tests...${NC}"
    response=$(curl -s -X POST "$BASE_URL/api/test/test-edge-cases")
    echo "$response" | jq '.'
}

# Custom team count
custom_team_count() {
    echo -e "\n${BOLD}🎯 Custom Team Count Test${NC}"
    echo -n "Enter number of teams (2-50): "
    read team_count
    
    if [[ ! "$team_count" =~ ^[0-9]+$ ]] || [ "$team_count" -lt 2 ] || [ "$team_count" -gt 50 ]; then
        echo -e "${RED}❌ Invalid team count. Please enter a number between 2 and 50.${NC}"
        return 1
    fi
    
    test_team_count "$team_count"
}

# Performance test
performance_test() {
    echo -e "\n${BOLD}🏃 Performance Test${NC}"
    echo "Testing fairness with various team counts..."
    
    for i in {4..20..2}; do
        echo -n "Testing $i teams... "
        response=$(curl -s -X GET "$BASE_URL/api/test/quick-demo/$i")
        is_fair=$(echo "$response" | jq -r '.results.isFair')
        
        if [ "$is_fair" = "true" ]; then
            echo -e "${GREEN}✅ Fair${NC}"
        else
            echo -e "${YELLOW}⚠️  Needs improvement${NC}"
        fi
    done
}

# Clean up test data
cleanup() {
    echo -e "\n${BOLD}🧹 Cleaning Up Test Data${NC}"
    response=$(curl -s -X DELETE "$BASE_URL/api/test/cleanup")
    echo "$response" | jq '.'
}

# View test data summary
summary() {
    echo -e "\n${BOLD}📋 Test Data Summary${NC}"
    curl -s -X GET "$BASE_URL/api/test/data-summary"
    echo ""
}

# Run unit tests
unit_tests() {
    echo -e "\n${BOLD}🧪 Running Unit Tests${NC}"
    if [ -f "./mvnw" ]; then
        ./mvnw test -Dtest=FairVotingServiceTest
    elif [ -f "../mvnw" ]; then
        ../mvnw test -Dtest=FairVotingServiceTest
    else
        echo -e "${RED}❌ Maven wrapper not found. Please run tests manually:${NC}"
        echo "mvn test -Dtest=FairVotingServiceTest"
    fi
}

# Main execution loop
while true; do
    show_menu
    read choice
    
    case $choice in
        1)
            quick_demo
            ;;
        2)
            multiple_scenarios
            ;;
        3)
            edge_cases
            ;;
        4)
            custom_team_count
            ;;
        5)
            performance_test
            ;;
        6)
            cleanup
            ;;
        7)
            summary
            ;;
        8)
            unit_tests
            ;;
        9)
            echo -e "\n${GREEN}👋 Thanks for testing the Fair Voting System!${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}❌ Invalid option. Please try again.${NC}"
            ;;
    esac
    
    echo -e "\n${YELLOW}Press Enter to continue...${NC}"
    read
done 