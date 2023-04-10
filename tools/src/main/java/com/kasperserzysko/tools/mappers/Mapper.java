package com.kasperserzysko.tools.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper implements IMapper{

    private final UserMapper userMapper;
    private final GameMapper gameMapper;
    private final RatingMapper ratingMapper;

    @Override
    public UserMapper getUserMapper() {
        return userMapper;
    }

    @Override
    public GameMapper getGameMapper() {
        return gameMapper;
    }

    @Override
    public RatingMapper getRatingMapper() {
        return ratingMapper;
    }
}
