package com.kasperserzysko.contracts.rating_dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RateCreateDto {

    private String comment;

    @Max(10)
    @Min(0)
    private int rating;

}
