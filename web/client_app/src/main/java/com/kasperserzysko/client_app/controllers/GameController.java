package com.kasperserzysko.client_app.controllers;

import com.kasperserzysko.contracts.game_dtos.GameDetailsDto;
import com.kasperserzysko.contracts.game_dtos.GameDto;
import com.kasperserzysko.contracts.game_dtos.GameRatingDto;
import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;
import com.kasperserzysko.contracts.rating_dtos.RatingDto;
import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Tag;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.tools.exceptions.FoundException;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.web.services.interfaces.IGameService;
import com.kasperserzysko.web.services.interfaces.IRatingService;
import com.kasperserzysko.web.services.interfaces.IUserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final IGameService gameService;
    private final IUserService userService;
    private final IRatingService ratingService;


    @GetMapping("/{id}")
    public ResponseEntity<GameDetailsDto> getGame(@PathVariable("id") Long gameId) throws NotFoundException {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }
    @GetMapping("/{id}/rating")
    public ResponseEntity<GameRatingDto> getGameRating(@PathVariable("id") Long gameId) throws NotFoundException {
        return ResponseEntity.ok(gameService.getGameRating(gameId));
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
                                                  @Param("sort") Optional<String> sort,
                                                  @Param("dir") Optional<String> dir){
        return ResponseEntity.ok(gameService.getGames(priceMax, priceMin, title, dateMax, dateMin, tags, genres, page, sort, dir));
    }
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long id) throws IOException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(gameService.getImage(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/rate")
    public ResponseEntity<?> rateGame(@PathVariable("id") Long gameId, @RequestBody RatingDetailsDto dto, @AuthenticationPrincipal SecurityUser loggedUser) throws NotFoundException, FoundException {
        userService.rateGame(dto, gameId, loggedUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/rate")
    public ResponseEntity<List<RatingDto>> getRatings(@PathVariable("id") Long gameId, @Param("page") Optional<Integer> page){
        return ResponseEntity.ok(ratingService.getRatings(gameId, page));
    }



    //
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ExpiredJwtException.class)
    public String handleExpiredJwtException() {
        return "Session finished! Log in again.";
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(IOException.class)
    public String handleIOException() {
        return "Couldn't load an image";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FoundException.class)
    public String handleFoundException(FoundException ex){
        return ex.getMessage();
    }

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public List<String> handleValidationExceptions(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage).toList();
    }
}
