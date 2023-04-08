package com.kasperserzysko.data.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class GameRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;
    private int rating;
    private LocalDate date;

    @ManyToOne
    private Game game;

    @ManyToOne
    private User user;
}
