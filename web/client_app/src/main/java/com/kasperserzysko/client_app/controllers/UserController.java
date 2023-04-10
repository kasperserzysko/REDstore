package com.kasperserzysko.client_app.controllers;

import com.kasperserzysko.contracts.user_dtos.UserDetailsDto;
import com.kasperserzysko.data.models.enums.Role;
import com.kasperserzysko.security.services.interfaces.IAuthenticationService;
import com.kasperserzysko.tools.exceptions.FoundException;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final IAuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDetailsDto dto) throws FoundException {
        authenticationService.registerUser(dto, Role.ROLE_USER);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDetailsDto dto){
        return ResponseEntity.ok(authenticationService.login(dto));
    }

    @PostMapping("/activate/{url}")
    public ResponseEntity<?> activateAccount(@PathVariable("url") String activationLink) throws NotFoundException {
        authenticationService.activate(activationLink);
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException() {
        return "Couldn't activate account!";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DisabledException.class)
    public String handleDisabledExceptions(DisabledException ex){
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FoundException.class)
    public String handleEmailFoundException(FoundException ex) {
        return ex.getMessage();
    }
}
