package com.kasperserzysko.tools.mappers;

import com.kasperserzysko.contracts.user_dtos.UserCredentialsDto;
import com.kasperserzysko.data.models.User;
import com.kasperserzysko.tools.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
public class UserMapper {

    @Autowired
    private Validator validator;

    public final BiConsumer<UserCredentialsDto, User> mapToEntity = (dto, user) ->{
        validator.validate(dto);
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
    };
}
