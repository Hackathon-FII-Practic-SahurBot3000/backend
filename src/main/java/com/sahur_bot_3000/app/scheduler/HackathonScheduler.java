package com.sahur_bot_3000.app.scheduler;

import com.sahur_bot_3000.app.model.Enums.HackathonState;
import com.sahur_bot_3000.app.model.Enums.HackathonType;
import com.sahur_bot_3000.app.model.Hackathon;
import com.sahur_bot_3000.app.repository.interfaces.HackathonRepository;
import com.sahur_bot_3000.app.service.HackathonService;
import com.sahur_bot_3000.app.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HackathonScheduler {

    private final HackathonRepository hackathonRepository;
    private final OpenAiService openAiService;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void updateHackathonStates() {
        Date now = new Date();
        List<Hackathon> hackathons = hackathonRepository.findAll();

        for (Hackathon h : hackathons) {
            if (h.getHackathonState() == HackathonState.Pending &&
                    now.after(new Date(h.getStartedAt().getTime() + 48L * 60 * 60 * 1000))) {
                h.setHackathonState(HackathonState.Ongoing);
                hackathonRepository.save(h);
            } else if (h.getHackathonState() == HackathonState.Ongoing &&
                    now.after(h.getEndedAt())) {
                h.setHackathonState(HackathonState.Ended);
                hackathonRepository.save(h);
            }
        }
    }



    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyHackathons() {
        for (HackathonType type : HackathonType.values()) {
            String[] theme = openAiService.generateTheme(type);
            String title = theme[0].trim();
            String description = theme[1].trim();

            Date now = new Date();
            Date end = new Date(now.getTime() + 48 * 60 * 60 * 1000); // +48h

            Hackathon h = Hackathon.builder()
                    .name(title)
                    .description(description)
                    .type(type)
                    .startedAt(now)
                    .endedAt(end)
                    .hackathonState(HackathonState.Pending)
                    .build();

            hackathonRepository.save(h);
        }
    }
}