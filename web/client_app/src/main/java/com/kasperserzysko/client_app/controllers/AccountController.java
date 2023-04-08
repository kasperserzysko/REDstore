package com.kasperserzysko.client_app.controllers;

import com.kasperserzysko.contracts.user_dtos.UserCredentialsDto;
import com.kasperserzysko.data.models.enums.Role;
import com.kasperserzysko.security.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserCredentialsDto dto){
        authenticationService.registerUser(dto, Role.ROLE_USER);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserCredentialsDto dto){
        return ResponseEntity.ok(authenticationService.login(dto));
    }

}
