package com.sahur_bot_3000.utils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentUtils {

    private final Environment environment;

    public EnvironmentUtils(Environment environment) {
        this.environment = environment;
    }

    public boolean isProduction() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("prod".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }
}
