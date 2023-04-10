package com.kasperserzysko.data.models;

import com.kasperserzysko.data.models.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    private String activationLink = UUID.randomUUID().toString();
    private boolean isEnabled = false;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();


    @OneToMany(mappedBy = "user")
    private List<Token> tokens = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private Set<GameRating> ratings = new HashSet<>();
}
