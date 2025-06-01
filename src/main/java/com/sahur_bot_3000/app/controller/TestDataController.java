package com.sahur_bot_3000.app.controller;

import com.sahur_bot_3000.app.dto.FairVotingAnalysisResponse;
import com.sahur_bot_3000.app.dto.FairVotingRequest;
import com.sahur_bot_3000.app.dto.VotingClusterResponse;
import com.sahur_bot_3000.app.service.FairVotingService;
import com.sahur_bot_3000.app.service.TestDataSeedingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestDataController {

    private final TestDataSeedingService seedingService;
    private final FairVotingService fairVotingService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Fair Voting Test API is running");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/seed-data/{numberOfTeams}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> seedTestData(@PathVariable int numberOfTeams) {
        Long hackathonId = seedingService.createTestHackathonWithTeams(numberOfTeams);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test data created successfully");
        response.put("hackathonId", hackathonId);
        response.put("numberOfTeams", numberOfTeams);
        response.put("nextStep", "Call POST /api/test/run-full-test/" + hackathonId + " to test the fair voting system");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/seed-multiple-scenarios")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> seedMultipleScenarios() {
        List<Long> hackathonIds = seedingService.createMultipleTestScenarios();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Multiple test scenarios created successfully");
        response.put("hackathonIds", hackathonIds);
        response.put("scenarios", Map.of(
            "Small (4 teams)", hackathonIds.get(0),
            "Medium (8 teams)", hackathonIds.get(1),
            "Large (12 teams)", hackathonIds.get(2),
            "Minimum (2 teams)", hackathonIds.get(3)
        ));
        response.put("nextStep", "Use the hackathon IDs to test the fair voting system");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/run-full-test/{hackathonId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> runFullTest(
            @PathVariable Long hackathonId,
            @RequestParam(defaultValue = "3") int teamsPerCluster,
            @RequestParam(defaultValue = "2") int candidatesPerCluster) {
        
        try {
            // Step 1: Generate fair voting clusters
            FairVotingRequest request = FairVotingRequest.builder()
                    .hackathonId(hackathonId)
                    .teamsPerCluster(teamsPerCluster)
                    .candidatesPerCluster(candidatesPerCluster)
                    .build();
            
            List<VotingClusterResponse> clusters = fairVotingService.generateFairVotingClusters(request);
            
            // Step 2: Analyze fairness
            FairVotingAnalysisResponse analysis = fairVotingService.analyzeFairness(hackathonId);
            
            // Step 3: Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Full test completed successfully");
            response.put("hackathonId", hackathonId);
            response.put("clustersGenerated", clusters.size());
            response.put("totalRounds", analysis.getTotalRounds());
            response.put("isFair", analysis.getIsFair());
            response.put("fairnessMessage", analysis.getFairnessMessage());
            response.put("clusters", clusters);
            response.put("analysis", analysis);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Test failed: " + e.getMessage());
            errorResponse.put("hackathonId", hackathonId);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/test-edge-cases")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> testEdgeCases() {
        Map<String, Object> results = new HashMap<>();
        
        try {
            // Test with minimum teams (2)
            Long hackathon2Teams = seedingService.createTestHackathonWithTeams(2);
            FairVotingRequest request2 = FairVotingRequest.builder()
                    .hackathonId(hackathon2Teams)
                    .teamsPerCluster(2)
                    .candidatesPerCluster(1)
                    .build();
            List<VotingClusterResponse> clusters2 = fairVotingService.generateFairVotingClusters(request2);
            FairVotingAnalysisResponse analysis2 = fairVotingService.analyzeFairness(hackathon2Teams);
            
            results.put("2_teams", Map.of(
                "hackathonId", hackathon2Teams,
                "clusters", clusters2.size(),
                "isFair", analysis2.getIsFair(),
                "message", analysis2.getFairnessMessage()
            ));
            
            // Test with odd number of teams (7)
            Long hackathon7Teams = seedingService.createTestHackathonWithTeams(7);
            FairVotingRequest request7 = FairVotingRequest.builder()
                    .hackathonId(hackathon7Teams)
                    .teamsPerCluster(3)
                    .candidatesPerCluster(2)
                    .build();
            List<VotingClusterResponse> clusters7 = fairVotingService.generateFairVotingClusters(request7);
            FairVotingAnalysisResponse analysis7 = fairVotingService.analyzeFairness(hackathon7Teams);
            
            results.put("7_teams", Map.of(
                "hackathonId", hackathon7Teams,
                "clusters", clusters7.size(),
                "isFair", analysis7.getIsFair(),
                "message", analysis7.getFairnessMessage()
            ));
            
            results.put("message", "Edge case testing completed successfully");
            
        } catch (Exception e) {
            results.put("error", "Edge case testing failed: " + e.getMessage());
        }
        
        return ResponseEntity.ok(results);
    }

    @GetMapping("/data-summary")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> getDataSummary() {
        seedingService.printTestDataSummary();
        return ResponseEntity.ok("Test data summary printed to logs. Check your console/log files.");
    }

    @DeleteMapping("/cleanup")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, String>> cleanupTestData() {
        seedingService.cleanupTestData();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Test data cleaned up successfully");
        response.put("note", "All test hackathons and associated teams have been deleted");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/quick-demo/{numberOfTeams}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> quickDemo(@PathVariable int numberOfTeams) {
        try {
            // Create test data
            Long hackathonId = seedingService.createTestHackathonWithTeams(numberOfTeams);
            
            // Generate voting clusters with default settings
            FairVotingRequest request = FairVotingRequest.builder()
                    .hackathonId(hackathonId)
                    .teamsPerCluster(Math.min(3, numberOfTeams))
                    .candidatesPerCluster(Math.min(2, numberOfTeams - 1))
                    .build();
            
            List<VotingClusterResponse> clusters = fairVotingService.generateFairVotingClusters(request);
            FairVotingAnalysisResponse analysis = fairVotingService.analyzeFairness(hackathonId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quick demo completed");
            response.put("setup", Map.of(
                "hackathonId", hackathonId,
                "numberOfTeams", numberOfTeams,
                "teamsPerCluster", request.getTeamsPerCluster(),
                "candidatesPerCluster", request.getCandidatesPerCluster()
            ));
            response.put("results", Map.of(
                "totalClusters", clusters.size(),
                "totalRounds", analysis.getTotalRounds(),
                "isFair", analysis.getIsFair(),
                "fairnessMessage", analysis.getFairnessMessage(),
                "recommendations", analysis.getRecommendations()
            ));
            response.put("clusters", clusters);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Quick demo failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 