package com.sahur_bot_3000.app.model;

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
    public UserType userType;

    @Column(name = "google_account", nullable = false)
    public boolean googleAccount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Link> links;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMember> teamMember;


    public enum UserType {
       business,admin, basic
    }


}