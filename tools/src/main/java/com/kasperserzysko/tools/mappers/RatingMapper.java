package com.kasperserzysko.tools.mappers;

import com.kasperserzysko.contracts.rating_dtos.RateCreateDto;
import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;
import com.kasperserzysko.data.models.GameRating;
import com.kasperserzysko.tools.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class RatingMapper {

    private final DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    private Validator validator;

    public final BiConsumer<RateCreateDto, GameRating> mapToEntity = (dto, rating) ->{
        validator.validate(dto);

        rating.setRating(dto.getRating());
        rating.setComment(dto.getComment());
    };

    public final Function<GameRating, RatingDetailsDto> mapToDetails = rating ->{
        var dto = new RatingDetailsDto();
        dto.setRating(rating.getRating());
        dto.setComment(rating.getComment());
        dto.setDate(rating.getDate().format(europeanDateFormatter));
        return dto;
    };
}
