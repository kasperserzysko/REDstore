package com.kasperserzysko.admin_app.controllers;

import com.kasperserzysko.contracts.user_dtos.UserCredentialsDto;
import com.kasperserzysko.data.models.enums.Role;
import com.kasperserzysko.security.services.interfaces.IAuthenticationService;
import com.kasperserzysko.tools.exceptions.FoundException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final IAuthenticationService authenticationService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody UserCredentialsDto dto) throws FoundException {
        authenticationService.registerUser(dto, Role.ROLE_ADMIN);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCredentialsDto dto){
        return ResponseEntity.ok(authenticationService.login(dto));
    }


    //

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ExpiredJwtException.class)
    public String handleExpiredJwtException() {
        return "Session finished! Log in again.";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public List<String> handleValidationExceptions(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage).toList();
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FoundException.class)
    public String handleEmailFoundException(FoundException ex) {
        return ex.getMessage();
    }
}
