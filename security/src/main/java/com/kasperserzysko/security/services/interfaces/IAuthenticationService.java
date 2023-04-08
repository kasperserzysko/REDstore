package com.kasperserzysko.security.services.interfaces;

import com.kasperserzysko.contracts.user_dtos.UserCredentialsDto;
import com.kasperserzysko.data.models.enums.Role;

public interface IAuthenticationService {

    void registerUser(UserCredentialsDto dto, Role role);
    String login(UserCredentialsDto dto);

}
