package com.kasperserzysko.client_app.controllers;

import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.tools.exceptions.PermissionDeniedException;
import com.kasperserzysko.web.services.RatingService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<RatingDetailsDto> getUpdateRating(@PathVariable("id") Long id, @AuthenticationPrincipal SecurityUser loggedUser) throws NotFoundException, PermissionDeniedException {
        return ResponseEntity.ok(ratingService.getUpdateRatingCredentials(id, loggedUser));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRating(@PathVariable("id") Long id, @RequestBody RatingDetailsDto dto, @AuthenticationPrincipal SecurityUser loggedUser) throws NotFoundException, PermissionDeniedException {
        ratingService.updateRating(dto, id, loggedUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRating(@PathVariable("id") Long id, @AuthenticationPrincipal SecurityUser loggedUser) throws NotFoundException, PermissionDeniedException {
        ratingService.deleteRating(id, loggedUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    //

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

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException() {
        return "Couldn't activate account!";
    }


}
