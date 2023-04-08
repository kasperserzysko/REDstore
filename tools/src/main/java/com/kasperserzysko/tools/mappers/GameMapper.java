package com.kasperserzysko.tools.mappers;

import com.kasperserzysko.contracts.game_dtos.GameCredentialsDto;
import com.kasperserzysko.contracts.game_dtos.GameDto;
import com.kasperserzysko.data.models.Game;
import com.kasperserzysko.tools.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class GameMapper {

    @Autowired
    private Validator validator;

    private final DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public final BiConsumer<GameCredentialsDto, Game> mapToEntity = (dto, game) ->{
        validator.validate(dto);
          game.setTitle(dto.getTitle());
          game.setDescription(dto.getDescription());
          game.setPrice(dto.getPrice());
          game.setReleaseDate(LocalDate.parse(dto.getReleaseDate(), europeanDateFormatter));
          game.setTags(dto.getTags());
          game.setGenres(dto.getGenres());
    };

    public final Function<Game, GameCredentialsDto> mapToCredentials = game -> {
        var dto = new GameCredentialsDto();
        dto.setTitle(game.getTitle());
        dto.setDescription(game.getDescription());
        dto.setReleaseDate(game.getReleaseDate().format(europeanDateFormatter));
        dto.setTags(game.getTags());
        dto.setPrice(game.getPrice());
        dto.setGenres(game.getGenres());
        return dto;
    };

    public final Function<Game, GameDto> mapToDto = game -> {
        var dto = new GameDto();
        dto.setId(game.getId());
        dto.setPrice(game.getPrice());
        dto.setTitle(game.getTitle());
        return dto;
    };
}

