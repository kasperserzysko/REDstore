package com.kasperserzysko.data.models;

import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Role;
import com.kasperserzysko.data.models.enums.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private float price;
    private LocalDate releaseDate;
    private float rating = 0;

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

    @OneToMany(mappedBy = "game")
    private Set<GameRating> ratings = new HashSet<>();

}
