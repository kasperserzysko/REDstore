package com.kasperserzysko.tools.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper implements IMapper{

    private final UserMapper userMapper;
    private final GameMapper gameMapper;

    @Override
    public UserMapper getUserMapper() {
        return userMapper;
    }

    @Override
    public GameMapper getGameMapper() {
        return gameMapper;
    }
}
