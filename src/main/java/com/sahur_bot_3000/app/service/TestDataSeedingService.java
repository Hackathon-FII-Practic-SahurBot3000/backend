package com.sahur_bot_3000.app.service;

import com.sahur_bot_3000.app.model.Enums.HackathonState;
import com.sahur_bot_3000.app.model.Enums.HackathonType;
import com.sahur_bot_3000.app.model.Hackathon;
import com.sahur_bot_3000.app.model.HackathonTeam;
import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.repository.interfaces.HackathonRepository;
import com.sahur_bot_3000.app.repository.interfaces.HackathonTeamRepository;
import com.sahur_bot_3000.app.repository.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestDataSeedingService {

    private final HackathonRepository hackathonRepository;
    private final HackathonTeamRepository hackathonTeamRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createTestHackathonWithTeams(int numberOfTeams) {
        log.info("Creating test hackathon with {} teams", numberOfTeams);

        // Create test user for hackathon creator
        User testUser = createTestUser("admin@test.com", "Test", "Admin");

        // Create test hackathon
        Hackathon hackathon = Hackathon.builder()
                .name("Test Hackathon - Fair Voting Demo")
                .description("A test hackathon to demonstrate the fair voting system with " + numberOfTeams + " teams")
                .type(HackathonType.BUSINESS) // Using existing enum value
                .hackathonState(HackathonState.Ongoing) // Using existing enum value
                .startedAt(new Date())
                .endedAt(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L)) // 7 days from now
                .createdBy(testUser)
                .build();

        hackathon = hackathonRepository.save(hackathon);
        log.info("Created hackathon with ID: {}", hackathon.getId());

        // Create test teams
        List<HackathonTeam> teams = createTestTeams(hackathon, numberOfTeams);
        log.info("Created {} teams for hackathon", teams.size());

        return hackathon.getId();
    }

    @Transactional
    public List<Long> createMultipleTestScenarios() {
        log.info("Creating multiple test scenarios for different team counts");
        
        List<Long> hackathonIds = new ArrayList<>();
        
        // Small hackathon (4 teams)
        hackathonIds.add(createTestHackathonWithTeams(4));
        
        // Medium hackathon (8 teams) 
        hackathonIds.add(createTestHackathonWithTeams(8));
        
        // Large hackathon (12 teams)
        hackathonIds.add(createTestHackathonWithTeams(12));
        
        // Edge case - minimum teams (2 teams)
        hackathonIds.add(createTestHackathonWithTeams(2));
        
        log.info("Created {} test hackathons", hackathonIds.size());
        return hackathonIds;
    }

    private User createTestUser(String email, String firstName, String lastName) {
        // Check if user already exists
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = User.builder()
                            .email(email)
                            .firstName(firstName)
                            .lastName(lastName)
                            .password("test123") // In real app, this should be encrypted
                            .profilePictureUrl("https://via.placeholder.com/150")
                            .role(com.sahur_bot_3000.app.model.Enums.Role.ADMIN)
                            .googleAccount(false)
                            .build();
                    return userRepository.save(user);
                });
    }

    private List<HackathonTeam> createTestTeams(Hackathon hackathon, int numberOfTeams) {
        List<HackathonTeam> teams = new ArrayList<>();
        
        String[] teamNames = {
            "Alpha Innovators", "Beta Builders", "Gamma Gurus", "Delta Developers",
            "Epsilon Engineers", "Zeta Zealots", "Eta Experts", "Theta Thinkers",
            "Iota Inventors", "Kappa Coders", "Lambda Leaders", "Mu Masters",
            "Nu Navigators", "Xi Xperts", "Omicron Optimizers", "Pi Pioneers",
            "Rho Revolutionaries", "Sigma Strategists", "Tau Technicians", "Upsilon United"
        };

        String[] submissionUrls = {
            "https://github.com/team1/awesome-project",
            "https://gitlab.com/team2/innovation-hub",
            "https://bitbucket.org/team3/game-changer",
            "https://github.com/team4/next-gen-app",
            "https://gitlab.com/team5/smart-solution",
            "https://github.com/team6/tech-breakthrough",
            "https://bitbucket.org/team7/future-platform",
            "https://github.com/team8/revolutionary-tool",
            "https://gitlab.com/team9/creative-engine",
            "https://github.com/team10/innovative-system",
            "https://bitbucket.org/team11/advanced-framework",
            "https://github.com/team12/cutting-edge-app",
            "https://gitlab.com/team13/smart-platform",
            "https://github.com/team14/tech-solution",
            "https://bitbucket.org/team15/modern-tool",
            "https://github.com/team16/digital-innovation",
            "https://gitlab.com/team17/web-revolution",
            "https://github.com/team18/mobile-breakthrough",
            "https://bitbucket.org/team19/ai-powered-app",
            "https://github.com/team20/cloud-native-solution"
        };

        for (int i = 0; i < numberOfTeams; i++) {
            String teamName = i < teamNames.length ? teamNames[i] : "Team " + (i + 1);
            String submissionUrl = i < submissionUrls.length ? submissionUrls[i] : 
                    "https://github.com/team" + (i + 1) + "/project";

            HackathonTeam team = HackathonTeam.builder()
                    .hackathon(hackathon)
                    .teamName(teamName)
                    .urlSubmission(submissionUrl)
                    .isJoined(true)
                    .teamMembers(new ArrayList<>()) // In a real scenario, you'd add team members here
                    .build();

            teams.add(hackathonTeamRepository.save(team));
        }

        return teams;
    }

    @Transactional
    public void cleanupTestData() {
        log.info("Cleaning up test data");
        
        // Find and delete test hackathons (those with "Test" in the name)
        List<Hackathon> testHackathons = hackathonRepository.findAll().stream()
                .filter(h -> h.getName().contains("Test Hackathon"))
                .toList();

        for (Hackathon hackathon : testHackathons) {
            // Delete associated teams first (due to foreign key constraints)
            List<HackathonTeam> teams = hackathonTeamRepository.findByHackathonId(hackathon.getId());
            hackathonTeamRepository.deleteAll(teams);
            
            // Delete the hackathon
            hackathonRepository.delete(hackathon);
            
            log.info("Deleted test hackathon: {} with {} teams", hackathon.getName(), teams.size());
        }

        log.info("Test data cleanup completed");
    }

    @Transactional(readOnly = true)
    public void printTestDataSummary() {
        List<Hackathon> testHackathons = hackathonRepository.findAll().stream()
                .filter(h -> h.getName().contains("Test Hackathon"))
                .toList();

        log.info("=== TEST DATA SUMMARY ===");
        log.info("Total test hackathons: {}", testHackathons.size());
        
        for (Hackathon hackathon : testHackathons) {
            List<HackathonTeam> teams = hackathonTeamRepository.findByHackathonId(hackathon.getId());
            log.info("Hackathon ID {}: '{}' - {} teams", 
                    hackathon.getId(), hackathon.getName(), teams.size());
            
            for (HackathonTeam team : teams) {
                log.info("  Team ID {}: '{}'", team.getId(), team.getTeamName());
            }
        }
        log.info("========================");
    }
} 