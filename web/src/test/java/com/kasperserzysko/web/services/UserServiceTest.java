package com.kasperserzysko.web.services;

import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;
import com.kasperserzysko.data.models.Game;
import com.kasperserzysko.data.models.GameRating;
import com.kasperserzysko.data.models.User;
import com.kasperserzysko.data.repositories.GameRepository;
import com.kasperserzysko.data.repositories.RatingRepository;
import com.kasperserzysko.data.repositories.UserRepository;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.tools.exceptions.FoundException;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.tools.mappers.IMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private UserRepository userRepository;

    @Autowired
    private IMapper mapper;

    private UserService userService;



    @BeforeEach
    void setUp(){
        userService = new UserService(gameRepository, ratingRepository, userRepository, mapper);
    }

    @Test
    void should_createRatingEntity() throws NotFoundException, FoundException {
        Long gameId = 1L;
        RatingDetailsDto dto = new RatingDetailsDto();
        dto.setRating(4);
        dto.setComment("comment");
        User userEntity = new User();
        userEntity.setId(1L);
        userEntity.setEmail("test@email");
        userEntity.setPassword("password");
        SecurityUser loggedUser = new SecurityUser(userEntity);

        Game expectedGame = new Game();
        expectedGame.setId(gameId);

        when(ratingRepository.findByUser_IdAndGame_Id(userEntity.getId(), gameId)).thenReturn(Optional.empty());
        when(userRepository.findUserWithRatings(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(expectedGame));

        userService.rateGame(dto, gameId, loggedUser);

        ArgumentCaptor<GameRating> ratingEntityCaptor = ArgumentCaptor.forClass(GameRating.class);
        verify(ratingRepository).save(ratingEntityCaptor.capture());

        GameRating ratingEntity = ratingEntityCaptor.getValue();
        assertThat(ratingEntity.getUser()).isEqualTo(userEntity);
        assertThat(ratingEntity.getGame().getId()).isEqualTo(gameId);
        assertThat(ratingEntity.getRating()).isEqualTo(dto.getRating());
        assertThat(ratingEntity.getComment()).isEqualTo(dto.getComment());
        assertThat(userEntity.getRatings()).contains(ratingEntity);
    }

    @Test
    void should_throwFoundException(){
        Long gameId = 1L;
        User userEntity = new User();
        userEntity.setId(1L);
        userEntity.setEmail("test@email");
        userEntity.setPassword("password");
        RatingDetailsDto dto = new RatingDetailsDto();
        dto.setRating(4);
        dto.setComment("comment");
        SecurityUser loggedUser = new SecurityUser(userEntity);

        when(ratingRepository.findByUser_IdAndGame_Id(userEntity.getId(), gameId)).thenReturn(Optional.of(new GameRating()));

        assertThrows(FoundException.class, () -> userService.rateGame(dto, gameId, loggedUser));
    }
}