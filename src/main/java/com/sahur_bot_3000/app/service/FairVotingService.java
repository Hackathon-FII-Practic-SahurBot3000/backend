package com.sahur_bot_3000.app.service;

import com.sahur_bot_3000.app.dto.*;
import com.sahur_bot_3000.app.model.*;
import com.sahur_bot_3000.app.model.Enums.VotingRole;
import com.sahur_bot_3000.app.repository.interfaces.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FairVotingService {
    
    private final VotingClusterRepository votingClusterRepository;
    private final VotingAssignmentRepository votingAssignmentRepository;
    private final HackathonRepository hackathonRepository;
    private final HackathonTeamRepository hackathonTeamRepository;

    @Transactional
    public List<VotingClusterResponse> generateFairVotingClusters(FairVotingRequest request) {
        log.info("Generating fair voting clusters for hackathon: {}", request.getHackathonId());
        
        // Validate hackathon exists
        Hackathon hackathon = hackathonRepository.findById(request.getHackathonId())
                .orElseThrow(() -> new EntityNotFoundException("Hackathon not found"));
        
        // Get all teams for this hackathon
        List<HackathonTeam> teams = hackathonTeamRepository.findByHackathonId(request.getHackathonId());
        
        if (teams.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 teams to create voting clusters");
        }
        
        // Calculate optimal cluster configuration
        ClusterConfiguration config = calculateOptimalConfiguration(teams.size(), request);
        log.info("Calculated optimal configuration: {}", config);
        
        // Generate the fair distribution matrix
        List<VotingRound> votingRounds = generateFairDistribution(teams, config);
        
        // Save clusters and assignments to database
        List<VotingClusterResponse> responses = new ArrayList<>();
        
        for (int roundIndex = 0; roundIndex < votingRounds.size(); roundIndex++) {
            VotingRound round = votingRounds.get(roundIndex);
            
            for (int clusterIndex = 0; clusterIndex < round.getClusters().size(); clusterIndex++) {
                ClusterAssignment cluster = round.getClusters().get(clusterIndex);
                
                // Create voting cluster entity
                VotingCluster votingCluster = VotingCluster.builder()
                        .hackathon(hackathon)
                        .clusterRound(roundIndex + 1)
                        .clusterName(String.format("Round %d - Cluster %d", roundIndex + 1, clusterIndex + 1))
                        .isActive(true)
                        .build();
                
                votingCluster = votingClusterRepository.save(votingCluster);
                
                // Create voting assignments
                List<VotingAssignment> assignments = new ArrayList<>();
                
                // Add voters
                for (int i = 0; i < cluster.getVoters().size(); i++) {
                    HackathonTeam voterTeam = cluster.getVoters().get(i);
                    VotingAssignment assignment = VotingAssignment.builder()
                            .votingCluster(votingCluster)
                            .team(voterTeam)
                            .votingRole(VotingRole.VOTER)
                            .assignmentOrder(i)
                            .build();
                    assignments.add(assignment);
                }
                
                // Add candidates
                for (int i = 0; i < cluster.getCandidates().size(); i++) {
                    HackathonTeam candidateTeam = cluster.getCandidates().get(i);
                    VotingAssignment assignment = VotingAssignment.builder()
                            .votingCluster(votingCluster)
                            .team(candidateTeam)
                            .votingRole(VotingRole.CANDIDATE)
                            .assignmentOrder(i)
                            .build();
                    assignments.add(assignment);
                }
                
                votingAssignmentRepository.saveAll(assignments);
                
                // Create response
                VotingClusterResponse response = VotingClusterResponse.builder()
                        .clusterId(votingCluster.getId())
                        .clusterName(votingCluster.getClusterName())
                        .clusterRound(votingCluster.getClusterRound())
                        .teamAssignments(assignments.stream()
                                .map(assignment -> VotingClusterResponse.TeamAssignment.builder()
                                        .teamId(assignment.getTeam().getId())
                                        .teamName(assignment.getTeam().getTeamName())
                                        .votingRole(assignment.getVotingRole())
                                        .assignmentOrder(assignment.getAssignmentOrder())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
                
                responses.add(response);
            }
        }
        
        log.info("Generated {} voting clusters across {} rounds", responses.size(), votingRounds.size());
        return responses;
    }

    private ClusterConfiguration calculateOptimalConfiguration(int totalTeams, FairVotingRequest request) {
        int teamsPerCluster = request.getTeamsPerCluster() != null ? request.getTeamsPerCluster() : 
                Math.max(2, Math.min(5, totalTeams / 3)); // Default: 2-5 teams per cluster
        
        int candidatesPerCluster = request.getCandidatesPerCluster() != null ? request.getCandidatesPerCluster() : 
                Math.max(2, Math.min(teamsPerCluster, totalTeams / 2)); // Default: up to half the teams as candidates
        
        // Calculate how many rounds we need to ensure fairness
        int minRounds = (int) Math.ceil((double) totalTeams / candidatesPerCluster);
        
        return ClusterConfiguration.builder()
                .totalTeams(totalTeams)
                .teamsPerCluster(teamsPerCluster)
                .candidatesPerCluster(candidatesPerCluster)
                .totalRounds(minRounds)
                .build();
    }

    private List<VotingRound> generateFairDistribution(List<HackathonTeam> teams, ClusterConfiguration config) {
        List<VotingRound> rounds = new ArrayList<>();
        
        // Track how many times each team has been a candidate
        Map<Long, Integer> candidateCount = new HashMap<>();
        teams.forEach(team -> candidateCount.put(team.getId(), 0));
        
        // Shuffle teams for random distribution
        List<HackathonTeam> shuffledTeams = new ArrayList<>(teams);
        Collections.shuffle(shuffledTeams);
        
        for (int round = 0; round < config.getTotalRounds(); round++) {
            VotingRound votingRound = generateRound(shuffledTeams, config, candidateCount, round);
            rounds.add(votingRound);
            
            // Update candidate counts
            for (ClusterAssignment cluster : votingRound.getClusters()) {
                for (HackathonTeam candidate : cluster.getCandidates()) {
                    candidateCount.put(candidate.getId(), candidateCount.get(candidate.getId()) + 1);
                }
            }
        }
        
        return rounds;
    }

    private VotingRound generateRound(List<HackathonTeam> teams, ClusterConfiguration config, 
                                    Map<Long, Integer> candidateCount, int roundNumber) {
        
        // Sort teams by how many times they've been candidates (least first for fairness)
        List<HackathonTeam> sortedForCandidates = teams.stream()
                .sorted(Comparator.comparing(team -> candidateCount.get(team.getId())))
                .collect(Collectors.toList());
        
        List<ClusterAssignment> clusters = new ArrayList<>();
        Set<Long> usedAsCandidate = new HashSet<>();
        Set<Long> usedAsVoter = new HashSet<>();
        
        int clustersNeeded = (int) Math.ceil((double) teams.size() / config.getTeamsPerCluster());
        
        for (int clusterIndex = 0; clusterIndex < clustersNeeded; clusterIndex++) {
            // Select candidates (prioritizing teams that have been candidates less often)
            List<HackathonTeam> candidates = sortedForCandidates.stream()
                    .filter(team -> !usedAsCandidate.contains(team.getId()))
                    .limit(config.getCandidatesPerCluster())
                    .collect(Collectors.toList());
            
            candidates.forEach(team -> usedAsCandidate.add(team.getId()));
            
            // Select voters (excluding current candidates)
            List<HackathonTeam> voters = teams.stream()
                    .filter(team -> !usedAsCandidate.contains(team.getId()) && !usedAsVoter.contains(team.getId()))
                    .limit(config.getTeamsPerCluster())
                    .collect(Collectors.toList());
            
            voters.forEach(team -> usedAsVoter.add(team.getId()));
            
            // If we don't have enough voters, fill from teams not being candidates in this cluster
            if (voters.size() < config.getTeamsPerCluster()) {
                List<HackathonTeam> additionalVoters = teams.stream()
                        .filter(team -> !candidates.contains(team) && !voters.contains(team))
                        .limit(config.getTeamsPerCluster() - voters.size())
                        .collect(Collectors.toList());
                voters.addAll(additionalVoters);
            }
            
            if (!candidates.isEmpty() && !voters.isEmpty()) {
                clusters.add(ClusterAssignment.builder()
                        .candidates(candidates)
                        .voters(voters)
                        .build());
            }
        }
        
        return VotingRound.builder()
                .roundNumber(roundNumber + 1)
                .clusters(clusters)
                .build();
    }

    @Transactional(readOnly = true)
    public FairVotingAnalysisResponse analyzeFairness(Long hackathonId) {
        List<VotingCluster> clusters = votingClusterRepository.findByHackathonIdAndIsActiveTrue(hackathonId);
        
        if (clusters.isEmpty()) {
            throw new IllegalArgumentException("No voting clusters found for hackathon");
        }
        
        Map<Long, FairVotingAnalysisResponse.TeamFairnessStats> teamStats = calculateTeamStats(hackathonId);
        
        // Determine if distribution is fair
        Collection<FairVotingAnalysisResponse.TeamFairnessStats> stats = teamStats.values();
        boolean isFair = isDistributionFair(stats);
        
        Integer maxRound = votingClusterRepository.findMaxClusterRoundByHackathonId(hackathonId);
        
        return FairVotingAnalysisResponse.builder()
                .hackathonId(hackathonId)
                .totalRounds(maxRound != null ? maxRound : 0)
                .isFair(isFair)
                .fairnessMessage(generateFairnessMessage(isFair, stats))
                .teamStats(teamStats)
                .recommendations(generateRecommendations(stats))
                .build();
    }

    private Map<Long, FairVotingAnalysisResponse.TeamFairnessStats> calculateTeamStats(Long hackathonId) {
        Map<Long, FairVotingAnalysisResponse.TeamFairnessStats> teamStats = new HashMap<>();
        
        // Get all teams
        List<HackathonTeam> teams = hackathonTeamRepository.findByHackathonId(hackathonId);
        
        for (HackathonTeam team : teams) {
            Long voterCount = votingAssignmentRepository.countByTeamIdAndVotingRole(team.getId(), VotingRole.VOTER);
            Long candidateCount = votingAssignmentRepository.countByTeamIdAndVotingRole(team.getId(), VotingRole.CANDIDATE);
            
            // Calculate fairness score (lower is better, 0 is perfect)
            double avgVoter = teams.stream().mapToLong(t -> 
                    votingAssignmentRepository.countByTeamIdAndVotingRole(t.getId(), VotingRole.VOTER))
                    .average().orElse(0.0);
            double avgCandidate = teams.stream().mapToLong(t -> 
                    votingAssignmentRepository.countByTeamIdAndVotingRole(t.getId(), VotingRole.CANDIDATE))
                    .average().orElse(0.0);
            
            double voterDeviation = Math.abs(voterCount - avgVoter);
            double candidateDeviation = Math.abs(candidateCount - avgCandidate);
            double fairnessScore = voterDeviation + candidateDeviation;
            
            teamStats.put(team.getId(), FairVotingAnalysisResponse.TeamFairnessStats.builder()
                    .teamId(team.getId())
                    .teamName(team.getTeamName())
                    .timesAsVoter(voterCount.intValue())
                    .timesAsCandidate(candidateCount.intValue())
                    .fairnessScore(fairnessScore)
                    .build());
        }
        
        return teamStats;
    }

    private boolean isDistributionFair(Collection<FairVotingAnalysisResponse.TeamFairnessStats> stats) {
        if (stats.isEmpty()) return true;
        
        // Check if all teams have similar voting opportunities (within 1 of each other)
        IntSummaryStatistics voterStats = stats.stream().mapToInt(s -> s.getTimesAsVoter()).summaryStatistics();
        IntSummaryStatistics candidateStats = stats.stream().mapToInt(s -> s.getTimesAsCandidate()).summaryStatistics();
        
        return (voterStats.getMax() - voterStats.getMin() <= 1) && 
               (candidateStats.getMax() - candidateStats.getMin() <= 1);
    }

    private String generateFairnessMessage(boolean isFair, Collection<FairVotingAnalysisResponse.TeamFairnessStats> stats) {
        if (isFair) {
            return "The voting distribution is fair. All teams have equal or nearly equal voting opportunities.";
        } else {
            IntSummaryStatistics voterStats = stats.stream().mapToInt(s -> s.getTimesAsVoter()).summaryStatistics();
            IntSummaryStatistics candidateStats = stats.stream().mapToInt(s -> s.getTimesAsCandidate()).summaryStatistics();
            
            return String.format("The voting distribution shows some imbalance. " +
                    "Voter assignments range from %d to %d, candidate assignments range from %d to %d.",
                    voterStats.getMin(), voterStats.getMax(), 
                    candidateStats.getMin(), candidateStats.getMax());
        }
    }

    private List<String> generateRecommendations(Collection<FairVotingAnalysisResponse.TeamFairnessStats> stats) {
        List<String> recommendations = new ArrayList<>();
        
        if (stats.isEmpty()) {
            recommendations.add("No teams found for analysis.");
            return recommendations;
        }
        
        IntSummaryStatistics voterStats = stats.stream().mapToInt(s -> s.getTimesAsVoter()).summaryStatistics();
        IntSummaryStatistics candidateStats = stats.stream().mapToInt(s -> s.getTimesAsCandidate()).summaryStatistics();
        
        if (voterStats.getMax() - voterStats.getMin() > 1) {
            recommendations.add("Consider adding more voting rounds to balance voter assignments.");
        }
        
        if (candidateStats.getMax() - candidateStats.getMin() > 1) {
            recommendations.add("Consider adjusting cluster size to ensure more equal candidate opportunities.");
        }
        
        if (voterStats.getAverage() < 2) {
            recommendations.add("Consider increasing the number of voting rounds to give teams more voting experience.");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("The current distribution is well-balanced. No changes recommended.");
        }
        
        return recommendations;
    }

    @Transactional(readOnly = true)
    public List<VotingClusterResponse> getVotingClusters(Long hackathonId) {
        List<VotingCluster> clusters = votingClusterRepository.findByHackathonIdAndIsActiveTrue(hackathonId);
        
        return clusters.stream()
                .map(cluster -> VotingClusterResponse.builder()
                        .clusterId(cluster.getId())
                        .clusterName(cluster.getClusterName())
                        .clusterRound(cluster.getClusterRound())
                        .teamAssignments(cluster.getVotingAssignments().stream()
                                .map(assignment -> VotingClusterResponse.TeamAssignment.builder()
                                        .teamId(assignment.getTeam().getId())
                                        .teamName(assignment.getTeam().getTeamName())
                                        .votingRole(assignment.getVotingRole())
                                        .assignmentOrder(assignment.getAssignmentOrder())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    // Helper classes for internal use
    @lombok.Data
    @lombok.Builder
    private static class ClusterConfiguration {
        private int totalTeams;
        private int teamsPerCluster;
        private int candidatesPerCluster;
        private int totalRounds;
    }

    @lombok.Data
    @lombok.Builder
    private static class VotingRound {
        private int roundNumber;
        private List<ClusterAssignment> clusters;
    }

    @lombok.Data
    @lombok.Builder
    private static class ClusterAssignment {
        private List<HackathonTeam> voters;
        private List<HackathonTeam> candidates;
    }
} 