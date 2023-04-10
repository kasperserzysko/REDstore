package com.kasperserzysko.tools.mappers;

import com.kasperserzysko.contracts.user_dtos.UserDetailsDto;
import com.kasperserzysko.data.models.User;
import com.kasperserzysko.tools.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
public class UserMapper {

    @Autowired
    private Validator validator;

    public final BiConsumer<UserDetailsDto, User> mapToEntity = (dto, user) ->{
        validator.validate(dto);
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
    };
}
