package com.kasperserzysko.data.models;

import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Role;
import com.kasperserzysko.data.models.enums.Tag;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private float price;

    @ElementCollection(targetClass = Tag.class)
    @JoinTable(name = "games_tags", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "tag", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Tag> tags = new HashSet<>();

    @ElementCollection(targetClass = Genre.class)
    @JoinTable(name = "games_genres", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "genre", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Genre> genres = new HashSet<>();



}
