package com.kasperserzysko.security.services.interfaces;

import com.kasperserzysko.contracts.user_dtos.UserCredentialsDto;
import com.kasperserzysko.data.models.enums.Role;
import com.kasperserzysko.tools.exceptions.FoundException;
import com.kasperserzysko.tools.exceptions.NotFoundException;

public interface IAuthenticationService {

    void registerUser(UserCredentialsDto dto, Role role) throws FoundException;
    String login(UserCredentialsDto dto);
    void activate(String activationLink) throws NotFoundException;
}
