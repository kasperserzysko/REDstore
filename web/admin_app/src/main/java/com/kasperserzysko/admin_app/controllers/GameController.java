package com.kasperserzysko.admin_app.controllers;

import com.kasperserzysko.contracts.game_dtos.GameDetailsDto;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.web.services.interfaces.IGameService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final IGameService gameService;


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> saveGame(@RequestPart("game") GameDetailsDto dto, @RequestPart("image") MultipartFile image) throws IOException {
        gameService.createGame(dto, image);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<GameDetailsDto> getGameUpdateCredentials(@PathVariable("id") Long id) throws NotFoundException {
        return ResponseEntity.ok(gameService.getGameUpdateCredentials(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGame(@RequestBody GameDetailsDto dto, @PathVariable("id") Long id) throws NotFoundException, IOException {
        gameService.updateGame(dto, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGame(@PathVariable("id") Long id) throws NotFoundException, IOException {
        gameService.deleteGame(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }



    //

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException ex){
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException ex){
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public List<String> handleValidationExceptions(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage).toList();
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ExpiredJwtException.class)
    public String handleExpiredJwtException() {
        return "Session finished! Log in again.";
    }
}
