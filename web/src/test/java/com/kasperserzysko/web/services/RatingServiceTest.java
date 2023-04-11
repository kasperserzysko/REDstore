package com.kasperserzysko.web.services;

import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;
import com.kasperserzysko.contracts.rating_dtos.RatingDto;
import com.kasperserzysko.data.models.Game;
import com.kasperserzysko.data.models.GameRating;
import com.kasperserzysko.data.models.User;
import com.kasperserzysko.data.repositories.RatingRepository;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.tools.exceptions.PermissionDeniedException;
import com.kasperserzysko.tools.mappers.IMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;
    @Autowired
    private IMapper mapper;

    private RatingService ratingService;

    @BeforeEach
    void setUp(){
        ratingService = new RatingService(ratingRepository, mapper);
    }

    @Test
    public void should_GetRatings() {
        Long gameId = 1L;
        GameRating rating1 = new GameRating ();
        rating1.setId(1L);
        rating1.setComment("Great game!");
        rating1.setRating(5);
        rating1.setGame(new Game());
        GameRating rating2 = new GameRating ();
        rating2.setId(2L);
        rating2.setComment("Not so good...");
        rating2.setRating(2);
        rating2.setGame(new Game());
        List<GameRating> ratings = List.of(rating1, rating2);

        when(ratingRepository.getRatings(eq(gameId), any(Pageable.class)))
                .thenReturn(ratings);

        List<RatingDto> result = ratingService.getRatings(gameId, Optional.empty());

        assertEquals(2, result.size());
        assertEquals("Great game!", result.get(0).getComment());
        assertEquals(5, result.get(0).getRating());
        assertEquals("Not so good...", result.get(1).getComment());
        assertEquals(2, result.get(1).getRating());
    }

    @Test
    void should_returnEmptyList() {
        Long gameId = 1L;
        Optional<Integer> page = Optional.of(0);

        List<RatingDto> ratings = ratingService.getRatings(gameId, page);

        assertTrue(ratings.isEmpty());
    }

    @Test
    public void should_updateRating() throws NotFoundException, PermissionDeniedException {
        RatingDetailsDto dto = new RatingDetailsDto();
        dto.setRating(8);
        dto.setComment("Great game!");

        GameRating ratingEntity = new GameRating();
        ratingEntity.setId(1L);
        ratingEntity.setRating(5);
        ratingEntity.setComment("Not bad");

        User user = new User();
        user.setId(1L);
        SecurityUser loggedUser = new SecurityUser(user);
        ratingEntity.setUser(user);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(ratingEntity));
        when(ratingRepository.save(any(GameRating.class))).thenReturn(ratingEntity);

        ratingService.updateRating(dto, 1L, loggedUser);

        assertEquals(dto.getRating(), ratingEntity.getRating());
        assertEquals(dto.getComment(), ratingEntity.getComment());
    }
    @Test
    public void should_throwNotFoundException() {
        RatingDetailsDto dto = new RatingDetailsDto();
        dto.setRating(8);
        dto.setComment("Great game!");

        SecurityUser loggedUser = new SecurityUser(new User());

        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> ratingService.updateRating(dto, 1L, loggedUser));
    }

    @Test
    public void should_throwPermissionDeniedException() {
        RatingDetailsDto dto = new RatingDetailsDto();
        dto.setRating(8);
        dto.setComment("Great game!");

        GameRating ratingEntity = new GameRating();
        ratingEntity.setId(1L);
        ratingEntity.setRating(5);
        ratingEntity.setComment("Not bad");

        User user = new User();
        user.setId(2L);
        SecurityUser loggedUser = new SecurityUser(user);
        ratingEntity.setUser(new User());
        ratingEntity.getUser().setId(1L);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(ratingEntity));

        assertThrows(PermissionDeniedException.class, () -> ratingService.updateRating(dto, 1L, loggedUser));
    }

    @Test
    public void should_deleteRating() throws NotFoundException, PermissionDeniedException {
        User user = new User();
        user.setId(1L);
        Game game = new Game();
        game.setId(1L);
        GameRating rating = new GameRating();
        rating.setId(1L);
        rating.setUser(user);
        rating.setGame(game);
        user.getRatings().add(rating);
        game.getRatings().add(rating);
        SecurityUser loggedUser = new SecurityUser(user);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        ratingService.deleteRating(1L, loggedUser);

        verify(ratingRepository, times(1)).delete(rating);
        assertNull(rating.getUser());
        assertNull(rating.getGame());
        assertFalse(user.getRatings().contains(rating));
        assertFalse(game.getRatings().contains(rating));
    }

    @Test
    public void should_throwNotFound(){
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());
       assertThrows(NotFoundException.class, () -> ratingService.deleteRating(1L, new SecurityUser(new User())));
    }

    @Test
    public void should_throwPermissionDenied() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        Game game = new Game();
        game.setId(1L);
        GameRating rating = new GameRating();
        rating.setId(1L);
        rating.setUser(user1);
        rating.setGame(game);
        user1.getRatings().add(rating);
        game.getRatings().add(rating);
        SecurityUser loggedUser = new SecurityUser(user2);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        assertThrows(PermissionDeniedException.class, () -> ratingService.deleteRating(1L, loggedUser));
    }
}