package com.sahur_bot_3000.app.model;

import com.sahur_bot_3000.app.model.Enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true)
    public String email;

    public String password;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "google_account", nullable = false)
    public boolean googleAccount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Link> links;

    @OneToMany(mappedBy = "user")
    private List<TeamMember> teamMembers;

    @OneToMany(mappedBy = "createdBy")
    private List<Hackathon> createdHackathons;
    
    public enum UserType {
       business,admin, basic
    }
}