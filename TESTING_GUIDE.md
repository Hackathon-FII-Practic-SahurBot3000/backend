# Fair Voting System Testing Guide

## Overview

This guide provides multiple ways to test the fair voting system, from automated unit tests to manual API testing and comprehensive integration scenarios.

## 🚀 Quick Start

### 1. Fastest Way - Quick Demo API

Start your Spring Boot application and call:

```bash
curl -X GET "http://localhost:8080/api/test/quick-demo/8"
```

This single endpoint will:
- Create a test hackathon with 8 teams
- Generate fair voting clusters
- Analyze fairness
- Return comprehensive results

### 2. Step-by-Step Manual Testing

```bash
# Step 1: Create test data
curl -X POST "http://localhost:8080/api/test/seed-data/6"

# Step 2: Use the returned hackathonId to generate clusters
curl -X POST "http://localhost:8080/api/test/run-full-test/{hackathonId}" \
  -H "Content-Type: application/json"

# Step 3: View results and analyze fairness
curl -X GET "http://localhost:8080/api/fair-voting/analyze/{hackathonId}"
```

## 🧪 Comprehensive Testing Scenarios

### Scenario 1: Multiple Team Sizes

```bash
# Test various team counts
curl -X POST "http://localhost:8080/api/test/seed-multiple-scenarios"
```

This creates 4 hackathons with:
- 2 teams (minimum case)
- 4 teams (small hackathon)
- 8 teams (medium hackathon)
- 12 teams (large hackathon)

### Scenario 2: Edge Cases

```bash
# Test edge cases automatically
curl -X POST "http://localhost:8080/api/test/test-edge-cases"
```

Tests:
- Minimum teams (2)
- Odd number of teams (7)
- Various cluster configurations

### Scenario 3: Custom Configuration Testing

```bash
# Test with custom parameters
curl -X POST "http://localhost:8080/api/test/run-full-test/{hackathonId}?teamsPerCluster=4&candidatesPerCluster=3"
```

## 🔧 API Testing Examples

### 1. Create Custom Test Data

```bash
# Create a hackathon with specific number of teams
curl -X POST "http://localhost:8080/api/test/seed-data/10" \
  -H "Content-Type: application/json"

# Response example:
{
  "message": "Test data created successfully",
  "hackathonId": 1,
  "numberOfTeams": 10,
  "nextStep": "Call POST /api/test/run-full-test/1 to test the fair voting system"
}
```

### 2. Generate Fair Voting Clusters

```bash
curl -X POST "http://localhost:8080/api/fair-voting/generate-clusters" \
  -H "Content-Type: application/json" \
  -d '{
    "hackathonId": 1,
    "teamsPerCluster": 3,
    "candidatesPerCluster": 2
  }'
```

### 3. Analyze Fairness

```bash
curl -X GET "http://localhost:8080/api/fair-voting/analyze/1"
```

### 4. Get Voting Clusters

```bash
curl -X GET "http://localhost:8080/api/fair-voting/clusters/1"
```

## 📊 Understanding Test Results

### Fair Distribution Example

```json
{
  "message": "Quick demo completed",
  "setup": {
    "hackathonId": 1,
    "numberOfTeams": 8,
    "teamsPerCluster": 3,
    "candidatesPerCluster": 2
  },
  "results": {
    "totalClusters": 8,
    "totalRounds": 4,
    "isFair": true,
    "fairnessMessage": "The voting distribution is fair. All teams have equal or nearly equal voting opportunities.",
    "recommendations": ["The current distribution is well-balanced. No changes recommended."]
  }
}
```

### Unfair Distribution Example

```json
{
  "isFair": false,
  "fairnessMessage": "The voting distribution shows some imbalance. Voter assignments range from 1 to 4, candidate assignments range from 1 to 3.",
  "recommendations": [
    "Consider adding more voting rounds to balance voter assignments.",
    "Consider adjusting cluster size to ensure more equal candidate opportunities."
  ]
}
```

## 🧪 Unit Testing

Run the automated tests:

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=FairVotingServiceTest

# Run with coverage
./mvnw test jacoco:report
```

### Key Test Cases

1. **Fair Distribution Test**: Verifies 8 teams get balanced assignments
2. **Minimum Teams Test**: Tests edge case with only 2 teams
3. **Insufficient Teams Test**: Ensures proper error handling
4. **Fairness Analysis Test**: Validates fairness calculation logic
5. **Cluster Retrieval Test**: Tests existing cluster fetching

## 🐛 Testing Edge Cases

### 1. Minimum Teams (2 teams)

```bash
curl -X GET "http://localhost:8080/api/test/quick-demo/2"
```

Expected: Should work with minimal configuration

### 2. Large Number of Teams (20 teams)

```bash
curl -X GET "http://localhost:8080/api/test/quick-demo/20"
```

Expected: Should create multiple rounds and clusters

### 3. Odd Number of Teams (7 teams)

```bash
curl -X GET "http://localhost:8080/api/test/quick-demo/7"
```

Expected: Should handle remainder teams gracefully

### 4. Single Team (Error Case)

```bash
curl -X GET "http://localhost:8080/api/test/quick-demo/1"
```

Expected: Should return error about insufficient teams

## 📈 Performance Testing

### Load Testing with Multiple Scenarios

```bash
# Create and test multiple hackathons
for i in {4..12}; do
  echo "Testing with $i teams..."
  curl -s -X GET "http://localhost:8080/api/test/quick-demo/$i" | jq '.results.isFair'
done
```

### Stress Testing

```bash
# Test with very large number of teams
curl -X GET "http://localhost:8080/api/test/quick-demo/50"
```

## 🧹 Cleanup

### Remove Test Data

```bash
# Clean up all test data
curl -X DELETE "http://localhost:8080/api/test/cleanup"
```

### View Current Test Data

```bash
# See what test data exists
curl -X GET "http://localhost:8080/api/test/data-summary"
```

## 📋 Testing Checklist

### Basic Functionality
- [ ] Can create test hackathons with teams
- [ ] Can generate fair voting clusters
- [ ] Can analyze fairness of distribution
- [ ] Can retrieve existing clusters

### Fairness Verification
- [ ] All teams appear as candidates equal times (±1)
- [ ] All teams get to vote approximately equal times
- [ ] No team votes for itself
- [ ] Distribution is mathematically balanced

### Edge Cases
- [ ] Works with 2 teams (minimum)
- [ ] Handles odd number of teams
- [ ] Rejects insufficient teams (< 2)
- [ ] Works with large team counts (20+)

### Error Handling
- [ ] Proper error messages for invalid inputs
- [ ] Graceful handling of missing data
- [ ] Appropriate HTTP status codes

### Performance
- [ ] Reasonable response times for large teams
- [ ] Memory usage within acceptable limits
- [ ] Database queries are optimized

## 🔍 Troubleshooting

### Common Issues

1. **"Hackathon not found"**
   - Make sure you're using the correct hackathon ID
   - Check if test data was created successfully

2. **"Need at least 2 teams"**
   - Verify team count in your test data
   - Use the data summary endpoint to check

3. **"No voting clusters found"**
   - Generate clusters first using the generate-clusters endpoint
   - Check if clusters were created successfully

### Debug Information

```bash
# Check application logs
tail -f logs/application.log

# View test data summary
curl -X GET "http://localhost:8080/api/test/data-summary"

# Check specific hackathon teams
curl -X GET "http://localhost:8080/api/hackathons/{id}/teams"
```

## 📱 Frontend Testing

If you have a frontend, you can test the integration:

```javascript
// Test the complete flow
async function testFairVoting() {
  // 1. Create test data
  const seedResponse = await fetch('/api/test/seed-data/8', { method: 'POST' });
  const seedData = await seedResponse.json();
  const hackathonId = seedData.hackathonId;
  
  // 2. Generate clusters
  const clustersResponse = await fetch('/api/fair-voting/generate-clusters', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      hackathonId: hackathonId,
      teamsPerCluster: 3,
      candidatesPerCluster: 2
    })
  });
  const clusters = await clustersResponse.json();
  
  // 3. Analyze fairness
  const analysisResponse = await fetch(`/api/fair-voting/analyze/${hackathonId}`);
  const analysis = await analysisResponse.json();
  
  console.log('Clusters:', clusters);
  console.log('Analysis:', analysis);
  console.log('Is Fair:', analysis.isFair);
}
```

This comprehensive testing approach ensures your fair voting system works correctly across all scenarios and edge cases. 