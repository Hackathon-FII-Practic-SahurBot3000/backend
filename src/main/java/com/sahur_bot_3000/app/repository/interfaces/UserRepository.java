package com.sahur_bot_3000.app.repository.interfaces;


import com.sahur_bot_3000.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findAllByRole(com.sahur_bot_3000.app.model.Enums.Role role);

    boolean existsByEmail(String email);
}
