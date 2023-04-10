package com.kasperserzysko.contracts.rating_dtos;

import lombok.Data;


@Data
public class RatingDto {

    private Long id;
    private String comment;
    private int rating;
    private String date;
}
