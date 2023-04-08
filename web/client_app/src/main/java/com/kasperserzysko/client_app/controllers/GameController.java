package com.kasperserzysko.client_app.controllers;

import com.kasperserzysko.contracts.game_dtos.GameCredentialsDto;
import com.kasperserzysko.contracts.game_dtos.GameDto;
import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Tag;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.web.services.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    @GetMapping("/{id}")
    public ResponseEntity<GameCredentialsDto> getGame(@PathVariable("id") Long gameId) throws NotFoundException {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }

    @GetMapping
    public ResponseEntity<List<GameDto>> getGames(@Param("priceMax") Optional<Float> priceMax,
                                                  @Param("priceMin")Optional<Float> priceMin,
                                                  @Param("title")Optional<String> title,
                                                  @Param("dateMax")Optional<String> dateMax,
                                                  @Param("dateMin")Optional<String> dateMin,
                                                  @Param("tags")Optional<Tag[]> tags,
                                                  @Param("genres")Optional<Genre[]> genres,
                                                  @Param("page")Optional<Integer> page,
                                                  @Param("sortBy") Optional<String> sort,
                                                  @Param("direction") Optional<String> direction){
        return ResponseEntity.ok(gameService.getGames(priceMax, priceMin, title, dateMax, dateMin, tags, genres, page, sort, direction));
    }






    //
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException ex){
        return ex.getMessage();
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateTimeParseException.class)
    public String handleDateTimeParseException(){
        return "Bad date format! Use dd.MM.yyyy";
    }
}
