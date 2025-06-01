package com.sahur_bot_3000.app.service;

import com.sahur_bot_3000.app.model.Enums.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    public Role determineRoleFromEmail(String email) {
        if (email.endsWith("@asii.com")) {
            return Role.BUSINESS;
        }
        return Role.USER;
    }
}
