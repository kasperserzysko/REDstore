package com.kasperserzysko.data.models;

import com.kasperserzysko.data.models.enums.Role;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @ElementCollection(targetClass = Role.class)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();


}
