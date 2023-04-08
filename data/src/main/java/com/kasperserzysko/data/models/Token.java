package com.kasperserzysko.data.models;

import com.kasperserzysko.data.models.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    public Integer id;

    @Column(unique = true)
    public String token;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    public boolean revoked;

    public boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;
}