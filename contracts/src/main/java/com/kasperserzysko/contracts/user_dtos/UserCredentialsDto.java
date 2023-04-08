package com.kasperserzysko.contracts.user_dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCredentialsDto {

    @NotNull(message = "Field: email can't be null!")
    @Email(message = "Field must be an email!")
    private String email;

    @NotNull(message = "Field: password can't be blank!")
    private String password;
}
