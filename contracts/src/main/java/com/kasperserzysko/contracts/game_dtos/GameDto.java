package com.kasperserzysko.contracts.game_dtos;

import lombok.Data;

@Data
public class GameDto {

    private Long id;
    private String title;
    private float price;
    private float rating;
}
