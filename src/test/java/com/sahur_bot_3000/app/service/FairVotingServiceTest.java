package com.sahur_bot_3000.app.service;

import com.sahur_bot_3000.app.dto.FairVotingAnalysisResponse;
import com.sahur_bot_3000.app.dto.FairVotingRequest;
import com.sahur_bot_3000.app.dto.VotingClusterResponse;
import com.sahur_bot_3000.app.model.Enums.HackathonState;
import com.sahur_bot_3000.app.model.Enums.HackathonType;
import com.sahur_bot_3000.app.model.Enums.VotingRole;
import com.sahur_bot_3000.app.model.Hackathon;
import com.sahur_bot_3000.app.model.HackathonTeam;
import com.sahur_bot_3000.app.repository.interfaces.HackathonRepository;
import com.sahur_bot_3000.app.repository.interfaces.HackathonTeamRepository;
import com.sahur_bot_3000.app.repository.interfaces.VotingAssignmentRepository;
import com.sahur_bot_3000.app.repository.interfaces.VotingClusterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FairVotingServiceTest {

    @Mock
    private VotingClusterRepository votingClusterRepository;

    @Mock
    private VotingAssignmentRepository votingAssignmentRepository;

    @Mock
    private HackathonRepository hackathonRepository;

    @Mock
    private HackathonTeamRepository hackathonTeamRepository;

    @InjectMocks
    private FairVotingService fairVotingService;

    private Hackathon testHackathon;
    private List<HackathonTeam> testTeams;

    @BeforeEach
    void setUp() {
        testHackathon = Hackathon.builder()
                .id(1L)
                .name("Test Hackathon")
                .type(HackathonType.BUSINESS)
                .hackathonState(HackathonState.Ongoing)
                .build();

        testTeams = createTestTeams(8); // 8 teams for testing
    }

    @Test
    void testGenerateFairVotingClusters_WithEightTeams_ShouldCreateFairDistribution() {
        // Given
        FairVotingRequest request = FairVotingRequest.builder()
                .hackathonId(1L)
                .teamsPerCluster(3)
                .candidatesPerCluster(2)
                .build();

        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(testHackathon));
        when(hackathonTeamRepository.findByHackathonId(1L)).thenReturn(testTeams);
        when(votingClusterRepository.save(any())).thenAnswer(invocation -> {
            var cluster = invocation.getArgument(0);
            // Simulate setting an ID on save
            return cluster;
        });

        // When
        List<VotingClusterResponse> clusters = fairVotingService.generateFairVotingClusters(request);

        // Then
        assertNotNull(clusters);
        assertFalse(clusters.isEmpty());
        
        // Verify that clusters were created
        verify(votingClusterRepository, atLeastOnce()).save(any());
        verify(votingAssignmentRepository, atLeastOnce()).saveAll(any());
        
        // Verify cluster structure
        clusters.forEach(cluster -> {
            assertNotNull(cluster.getClusterName());
            assertNotNull(cluster.getTeamAssignments());
            assertFalse(cluster.getTeamAssignments().isEmpty());
            
            // Check that we have both voters and candidates
            boolean hasVoters = cluster.getTeamAssignments().stream()
                    .anyMatch(assignment -> assignment.getVotingRole() == VotingRole.VOTER);
            boolean hasCandidates = cluster.getTeamAssignments().stream()
                    .anyMatch(assignment -> assignment.getVotingRole() == VotingRole.CANDIDATE);
            
            assertTrue(hasVoters, "Cluster should have voters");
            assertTrue(hasCandidates, "Cluster should have candidates");
        });
    }

    @Test
    void testGenerateFairVotingClusters_WithMinimumTeams_ShouldWork() {
        // Given
        List<HackathonTeam> minimumTeams = createTestTeams(2);
        FairVotingRequest request = FairVotingRequest.builder()
                .hackathonId(1L)
                .teamsPerCluster(2)
                .candidatesPerCluster(1)
                .build();

        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(testHackathon));
        when(hackathonTeamRepository.findByHackathonId(1L)).thenReturn(minimumTeams);
        when(votingClusterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<VotingClusterResponse> clusters = fairVotingService.generateFairVotingClusters(request);

        // Then
        assertNotNull(clusters);
        assertFalse(clusters.isEmpty());
    }

    @Test
    void testGenerateFairVotingClusters_WithInsufficientTeams_ShouldThrowException() {
        // Given
        List<HackathonTeam> insufficientTeams = createTestTeams(1);
        FairVotingRequest request = FairVotingRequest.builder()
                .hackathonId(1L)
                .teamsPerCluster(2)
                .candidatesPerCluster(1)
                .build();

        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(testHackathon));
        when(hackathonTeamRepository.findByHackathonId(1L)).thenReturn(insufficientTeams);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> fairVotingService.generateFairVotingClusters(request));
        
        assertEquals("Need at least 2 teams to create voting clusters", exception.getMessage());
    }

    @Test
    void testAnalyzeFairness_WithBalancedDistribution_ShouldReturnFair() {
        // Given
        Long hackathonId = 1L;
        
        // Mock teams
        when(hackathonTeamRepository.findByHackathonId(hackathonId)).thenReturn(testTeams);
        
        // Mock voting assignments - simulate fair distribution
        when(votingAssignmentRepository.countByTeamIdAndVotingRole(anyLong(), eq(VotingRole.VOTER)))
                .thenReturn(2L); // Each team votes 2 times
        when(votingAssignmentRepository.countByTeamIdAndVotingRole(anyLong(), eq(VotingRole.CANDIDATE)))
                .thenReturn(2L); // Each team is candidate 2 times
        
        // Mock clusters exist
        when(votingClusterRepository.findByHackathonIdAndIsActiveTrue(hackathonId))
                .thenReturn(Arrays.asList(mock(com.sahur_bot_3000.app.model.VotingCluster.class)));
        when(votingClusterRepository.findMaxClusterRoundByHackathonId(hackathonId))
                .thenReturn(4);

        // When
        FairVotingAnalysisResponse analysis = fairVotingService.analyzeFairness(hackathonId);

        // Then
        assertNotNull(analysis);
        assertEquals(hackathonId, analysis.getHackathonId());
        assertTrue(analysis.getIsFair());
        assertNotNull(analysis.getFairnessMessage());
        assertNotNull(analysis.getTeamStats());
        assertNotNull(analysis.getRecommendations());
        assertEquals(4, analysis.getTotalRounds());
    }

    @Test
    void testAnalyzeFairness_WithUnbalancedDistribution_ShouldReturnUnfair() {
        // Given
        Long hackathonId = 1L;
        
        when(hackathonTeamRepository.findByHackathonId(hackathonId)).thenReturn(testTeams);
        
        // Mock unbalanced distribution
        when(votingAssignmentRepository.countByTeamIdAndVotingRole(1L, VotingRole.VOTER))
                .thenReturn(1L);
        when(votingAssignmentRepository.countByTeamIdAndVotingRole(2L, VotingRole.VOTER))
                .thenReturn(4L); // Significant imbalance
        
        // Default for other teams
        when(votingAssignmentRepository.countByTeamIdAndVotingRole(anyLong(), eq(VotingRole.VOTER)))
                .thenReturn(2L);
        when(votingAssignmentRepository.countByTeamIdAndVotingRole(anyLong(), eq(VotingRole.CANDIDATE)))
                .thenReturn(2L);
        
        when(votingClusterRepository.findByHackathonIdAndIsActiveTrue(hackathonId))
                .thenReturn(Arrays.asList(mock(com.sahur_bot_3000.app.model.VotingCluster.class)));
        when(votingClusterRepository.findMaxClusterRoundByHackathonId(hackathonId))
                .thenReturn(3);

        // When
        FairVotingAnalysisResponse analysis = fairVotingService.analyzeFairness(hackathonId);

        // Then
        assertNotNull(analysis);
        assertFalse(analysis.getIsFair());
        assertTrue(analysis.getFairnessMessage().contains("imbalance"));
        assertTrue(analysis.getRecommendations().size() > 0);
    }

    @Test
    void testAnalyzeFairness_WithNoClusters_ShouldThrowException() {
        // Given
        Long hackathonId = 1L;
        when(votingClusterRepository.findByHackathonIdAndIsActiveTrue(hackathonId))
                .thenReturn(Collections.emptyList());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fairVotingService.analyzeFairness(hackathonId));
        
        assertEquals("No voting clusters found for hackathon", exception.getMessage());
    }

    @Test
    void testGetVotingClusters_ShouldReturnExistingClusters() {
        // Given
        Long hackathonId = 1L;
        
        var mockCluster = mock(com.sahur_bot_3000.app.model.VotingCluster.class);
        when(mockCluster.getId()).thenReturn(1L);
        when(mockCluster.getClusterName()).thenReturn("Test Cluster");
        when(mockCluster.getClusterRound()).thenReturn(1);
        when(mockCluster.getVotingAssignments()).thenReturn(Collections.emptyList());
        
        when(votingClusterRepository.findByHackathonIdAndIsActiveTrue(hackathonId))
                .thenReturn(Arrays.asList(mockCluster));

        // When
        List<VotingClusterResponse> clusters = fairVotingService.getVotingClusters(hackathonId);

        // Then
        assertNotNull(clusters);
        assertEquals(1, clusters.size());
        assertEquals("Test Cluster", clusters.get(0).getClusterName());
        assertEquals(1, clusters.get(0).getClusterRound());
    }

    private List<HackathonTeam> createTestTeams(int count) {
        List<HackathonTeam> teams = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            HackathonTeam team = HackathonTeam.builder()
                    .id((long) i)
                    .teamName("Team " + i)
                    .hackathon(testHackathon)
                    .isJoined(true)
                    .urlSubmission("https://github.com/team" + i + "/project")
                    .build();
            teams.add(team);
        }
        return teams;
    }
} 