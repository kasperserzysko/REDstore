package com.kasperserzysko.web.services;

import com.kasperserzysko.contracts.rating_dtos.RateCreateDto;
import com.kasperserzysko.data.models.GameRating;
import com.kasperserzysko.data.repositories.GameRepository;
import com.kasperserzysko.data.repositories.RatingRepository;
import com.kasperserzysko.data.repositories.UserRepository;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.tools.exceptions.FoundException;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.tools.mappers.IMapper;
import com.kasperserzysko.web.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements IUserService {

    private final GameRepository gameRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    private final IMapper mapper;

    @Override
    public void rateGame(RateCreateDto dto, Long gameId, SecurityUser loggedUser) throws NotFoundException, FoundException {
        var userEntity = loggedUser.user();
        if (ratingRepository.findByUser_IdAndGame_Id(userEntity.getId(), gameId).isPresent()){
            throw new FoundException("You've already rated this game!");
        }
        var userWithRatings = userRepository
                .findUserWithRatings(userEntity.getId())
                .orElseThrow(() -> new NotFoundException("Something went wrong!"));
        var gameEntity = gameRepository
                .findById(gameId)
                .orElseThrow(() -> new NotFoundException("Couldn't find game with id: " + gameId));

        var ratingEntity = new GameRating();
        mapper
                .getRatingMapper()
                .mapToEntity.accept(dto, ratingEntity);
        ratingEntity.setUser(userWithRatings);
        userWithRatings.getRatings().add(ratingEntity);

        ratingEntity.setGame(gameEntity);
        gameEntity.getRatings().add(ratingEntity);

        ratingRepository.save(ratingEntity);
    }
}
