# Fair Voting System Documentation

## Overview

The Fair Voting System ensures that all teams in a hackathon have equal opportunities to vote and be voted upon. The system creates voting clusters where teams are distributed fairly across multiple rounds, guaranteeing that each team appears as a candidate for voting the same number of times.

## Key Features

### 1. Fair Distribution Algorithm
- **Equal Candidate Opportunities**: Every team appears as a candidate for voting the same number of times (±1 for mathematical constraints)
- **Balanced Voter Assignments**: Teams are distributed as voters across different clusters to ensure fair participation
- **Round-Robin Style**: Multiple rounds ensure comprehensive coverage and fairness

### 2. Configurable Parameters
- **Teams Per Cluster**: Number of teams that vote in each cluster (default: 2-5 based on total teams)
- **Candidates Per Cluster**: Number of teams being voted on in each cluster (default: up to half the total teams)
- **Minimum Voting Rounds**: Ensures each team gets adequate voting experience

### 3. Fairness Analysis
- **Statistical Analysis**: Calculates fairness scores based on deviation from ideal distribution
- **Recommendations**: Provides suggestions for improving fairness
- **Real-time Monitoring**: Tracks voting distribution across all rounds

## Database Schema

### VotingCluster
```sql
CREATE TABLE voting_clusters (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hackathon_id BIGINT NOT NULL,
    cluster_round INTEGER NOT NULL,
    cluster_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (hackathon_id) REFERENCES hackathons(id)
);
```

### VotingAssignment
```sql
CREATE TABLE voting_assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    voting_cluster_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    voting_role ENUM('VOTER', 'CANDIDATE') NOT NULL,
    assignment_order INTEGER,
    FOREIGN KEY (voting_cluster_id) REFERENCES voting_clusters(id),
    FOREIGN KEY (team_id) REFERENCES hackathon_teams(id)
);
```

## API Endpoints

### 1. Generate Fair Voting Clusters
```http
POST /api/fair-voting/generate-clusters
Content-Type: application/json

{
    "hackathonId": 1,
    "teamsPerCluster": 3,
    "candidatesPerCluster": 2,
    "minVotingRounds": 2
}
```

**Response:**
```json
[
    {
        "clusterId": 1,
        "clusterName": "Round 1 - Cluster 1",
        "clusterRound": 1,
        "teamAssignments": [
            {
                "teamId": 1,
                "teamName": "Team Alpha",
                "votingRole": "VOTER",
                "assignmentOrder": 0
            },
            {
                "teamId": 2,
                "teamName": "Team Beta",
                "votingRole": "CANDIDATE",
                "assignmentOrder": 0
            }
        ]
    }
]
```

### 2. Analyze Fairness
```http
GET /api/fair-voting/analyze/{hackathonId}
```

**Response:**
```json
{
    "hackathonId": 1,
    "totalRounds": 3,
    "isFair": true,
    "fairnessMessage": "The voting distribution is fair. All teams have equal or nearly equal voting opportunities.",
    "teamStats": {
        "1": {
            "teamId": 1,
            "teamName": "Team Alpha",
            "timesAsVoter": 2,
            "timesAsCandidate": 2,
            "fairnessScore": 0.0
        }
    },
    "recommendations": [
        "The current distribution is well-balanced. No changes recommended."
    ]
}
```

### 3. Get Existing Clusters
```http
GET /api/fair-voting/clusters/{hackathonId}
```

## Algorithm Details

### Fair Distribution Algorithm

1. **Initialization**
   - Get all teams for the hackathon
   - Calculate optimal cluster configuration based on team count and parameters
   - Initialize candidate count tracking for fairness

2. **Round Generation**
   - For each round, prioritize teams that have been candidates least often
   - Distribute teams into clusters ensuring no team votes for itself
   - Balance voter and candidate assignments

3. **Fairness Enforcement**
   - Track how many times each team appears as a candidate
   - Ensure equal distribution (±1 for mathematical constraints)
   - Rotate voter assignments across rounds

### Example Scenario

**8 Teams, 3 Teams per Cluster, 2 Candidates per Cluster:**

**Round 1:**
- Cluster 1: Voters [Team1, Team2, Team3] → Candidates [Team4, Team5]
- Cluster 2: Voters [Team6, Team7, Team8] → Candidates [Team1, Team2]

**Round 2:**
- Cluster 1: Voters [Team4, Team5, Team1] → Candidates [Team6, Team7]
- Cluster 2: Voters [Team2, Team3, Team8] → Candidates [Team3, Team8]

This ensures each team appears as a candidate exactly twice across all rounds.

## Usage Examples

### Basic Usage
```java
@Autowired
private FairVotingService fairVotingService;

// Generate fair voting clusters
FairVotingRequest request = FairVotingRequest.builder()
    .hackathonId(1L)
    .teamsPerCluster(3)
    .candidatesPerCluster(2)
    .build();

List<VotingClusterResponse> clusters = fairVotingService.generateFairVotingClusters(request);

// Analyze fairness
FairVotingAnalysisResponse analysis = fairVotingService.analyzeFairness(1L);
```

### Frontend Integration
```javascript
// Generate clusters
const response = await fetch('/api/fair-voting/generate-clusters', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        hackathonId: 1,
        teamsPerCluster: 3,
        candidatesPerCluster: 2
    })
});

const clusters = await response.json();

// Display voting clusters to users
clusters.forEach(cluster => {
    console.log(`${cluster.clusterName}:`);
    cluster.teamAssignments.forEach(assignment => {
        console.log(`  ${assignment.teamName} - ${assignment.votingRole}`);
    });
});
```

## Benefits

1. **Guaranteed Fairness**: Mathematical guarantee that all teams get equal voting opportunities
2. **Scalable**: Works with any number of teams (minimum 2)
3. **Configurable**: Adjustable parameters for different hackathon sizes and requirements
4. **Transparent**: Full analysis and reporting of fairness metrics
5. **Automated**: No manual intervention required for fair distribution

## Best Practices

1. **Team Count Considerations**: 
   - Minimum 2 teams required
   - Optimal performance with 6+ teams
   - Large hackathons (50+ teams) may need multiple voting phases

2. **Parameter Tuning**:
   - Keep `teamsPerCluster` between 2-5 for manageable voting
   - Set `candidatesPerCluster` to 2-3 for focused evaluation
   - Ensure `minVotingRounds` ≥ 2 for meaningful participation

3. **Monitoring**:
   - Use the analysis endpoint to verify fairness
   - Check recommendations for optimization suggestions
   - Monitor fairness scores (lower is better, 0 is perfect)

## Error Handling

The system handles various edge cases:
- Insufficient teams (< 2)
- Invalid parameters
- Database constraints
- Concurrent access during cluster generation

All errors are returned with appropriate HTTP status codes and descriptive messages. 