package com.sahur_bot_3000.app.scheduler;

import com.sahur_bot_3000.app.model.Enums.HackathonState;
import com.sahur_bot_3000.app.model.Enums.HackathonType;
import com.sahur_bot_3000.app.model.Hackathon;
import com.sahur_bot_3000.app.repository.interfaces.HackathonRepository;
import com.sahur_bot_3000.app.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HackathonScheduler {
    private final HackathonRepository hackathonRepository;
    private final AiService aiService;

//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void generateDailyHackathons() {
        log.info("Starting daily hackathon generation");

        Arrays.stream(HackathonType.values()).forEach(type -> {
            try {
                String prompt = String.format("""
                        Generate an interesting theme for a %s hackathon.
                        Return only the title and an interesting description of 2-3 lines that is captivating and gives participants freedom to create something diverse and interesting, separated by `||`.
                        Ex: "Theme Name" || "Short description"
                        """, type.name());

                String response = aiService.generateResponse(prompt);
                String[] parts = response.split("\\|\\|");

                if (parts.length != 2) {
                    log.error("Invalid response format for type {}: {}", type, response);
                    return;
                }

                String name = parts[0].trim();
                String description = parts[1].trim();

                LocalDateTime startTime = LocalDateTime.now().plusDays(1);
                LocalDateTime endTime = LocalDateTime.now().plusDays(8);

                Hackathon hackathon = Hackathon.builder()
                        .name(name)
                        .description(description)
                        .type(type)
                        .hackathonState(HackathonState.Pending)
                        .startedAt(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()))
                        .endedAt(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()))
                        .prize("poll")  // Prize is determined by poll
                        .build();

                hackathonRepository.save(hackathon);
                log.info("Created new hackathon: {} of type {}", name, type);
            } catch (Exception e) {
                log.error("Error generating hackathon for type {}: {}", type, e.getMessage());
            }
        });

        log.info("Finished daily hackathon generation");
    }

    @Scheduled(fixedRate = 60 * 60 * 1000) // Runs every hour
    @Transactional
    public void updateHackathonStates() {
        log.info("Starting hackathon state update");
        Date now = new Date();
        List<Hackathon> hackathons = hackathonRepository.findAll();

        for (Hackathon hackathon : hackathons) {
            if (hackathon.getHackathonState() == HackathonState.Ended) continue;

            long diffMillis = now.getTime() - hackathon.getStartedAt().getTime();
            long hoursPassed = diffMillis / (1000 * 60 * 60);

            if (hoursPassed >= 96 && hackathon.getHackathonState() != HackathonState.Ended) {
                hackathon.setHackathonState(HackathonState.Ended);
                hackathonRepository.save(hackathon);
                log.info("Updated hackathon {} to Ended state", hackathon.getName());
            } else if (hoursPassed >= 48 && hackathon.getHackathonState() == HackathonState.Pending) {
                hackathon.setHackathonState(HackathonState.Ongoing);
                hackathonRepository.save(hackathon);
                log.info("Updated hackathon {} to Ongoing state", hackathon.getName());
            }
        }
        log.info("Finished hackathon state update");
    }


}