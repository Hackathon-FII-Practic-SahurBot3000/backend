package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    List<Link> findByUserId(Long userId);
} 