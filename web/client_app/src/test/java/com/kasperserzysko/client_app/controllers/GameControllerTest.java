package com.kasperserzysko.client_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasperserzysko.contracts.game_dtos.GameDetailsDto;
import com.kasperserzysko.contracts.game_dtos.GameDto;
import com.kasperserzysko.contracts.game_dtos.GameRatingDto;
import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;
import com.kasperserzysko.contracts.rating_dtos.RatingDto;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.tools.exceptions.FoundException;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.web.services.interfaces.IGameService;
import com.kasperserzysko.web.services.interfaces.IRatingService;
import com.kasperserzysko.web.services.interfaces.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IGameService gameService;
    @MockBean
    private IUserService userService;
    @MockBean
    private IRatingService ratingService;

    @Test
    void shouldReturnGameDetailsDto() throws Exception {
        Long gameId = 1L;
        GameDetailsDto gameDetailsDto = new GameDetailsDto();
        gameDetailsDto.setTitle("Test Game");
        gameDetailsDto.setDescription("This is a test game.");

        given(gameService.getGame(gameId)).willReturn(gameDetailsDto);

        mockMvc.perform(get("/games/" + gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(gameDetailsDto.getTitle()))
                .andExpect(jsonPath("$.description").value(gameDetailsDto.getDescription()));

        verify(gameService, times(1)).getGame(gameId);
    }
    @Test
    void shouldReturnNotFoundWhenGameIsNotFound() throws Exception {
        Long gameId = 1L;
        given(gameService.getGame(gameId)).willThrow(new NotFoundException("Game not found."));

        mockMvc.perform(get("/games/" + gameId))
                .andExpect(status().isNotFound());

        verify(gameService, times(1)).getGame(gameId);
    }

    @Test
    public void testGetGameRating() throws Exception {
        Long gameId = 1L;
        GameRatingDto gameRatingDto = new GameRatingDto(5);
        given(gameService.getGameRating(gameId)).willReturn(gameRatingDto);

        mockMvc.perform(get("/games/{id}/rating", gameId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rating").value(gameRatingDto.rating()));

        verify(gameService, times(1)).getGameRating(gameId);
    }
    @Test
    public void testGetGameRatingNotFound() throws Exception {
        Long gameId = 1L;
        given(gameService.getGameRating(gameId)).willThrow(new NotFoundException("Game not found."));

        mockMvc.perform(get("/games/{id}/rating", gameId))
                .andExpect(status().isNotFound());

        verify(gameService, times(1)).getGameRating(gameId);
    }

    @Test
    public void testGetGames() throws Exception {
        List<GameDto> gameList = new ArrayList<>();
        GameDto game1 = new GameDto();
        game1.setId(1L);
        game1.setTitle("Game 1");
        game1.setPrice(9.99f);
        gameList.add(game1);
        GameDto game2 = new GameDto();
        game2.setId(2L);
        game2.setTitle("Game 2");
        game2.setPrice(19.99f);
        gameList.add(game2);
        given(gameService.getGames(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).willReturn(gameList);

        mockMvc.perform(get("/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value((1)))
                .andExpect(jsonPath("$[0].title").value(("Game 1")))
                .andExpect(jsonPath("$[0].price").value((9.99)))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Game 2"))
                .andExpect(jsonPath("$[1].price").value(19.99));
    }
    @Test
    public void getImage_ShouldReturnOkAndImageContent() throws Exception {
        Long gameId = 1L;
        byte[] imageBytes = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
        given(gameService.getImage(gameId)).willReturn(imageBytes);

        mockMvc.perform(get("/games/" + gameId + "/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    @WithMockUser
    public void rateGame_authenticatedUser_returnsOk() throws Exception {
        RatingDetailsDto dto = new RatingDetailsDto();
        dto.setRating(5);
        dto.setComment("Great game!");

        mockMvc.perform(post("/games/{id}/rate", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userService).rateGame(eq(dto), eq(1L), any(SecurityUser.class));
    }
    @Test
    public void rateGame_unauthenticatedUser_returnsUnauthorized() throws Exception {
        RatingDetailsDto dto = new RatingDetailsDto();
        dto.setRating(5);
        dto.setComment("Great game!");

        mockMvc.perform(post("/games/{id}/rate", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verify(userService, never()).rateGame(eq(dto), eq(1L), any(SecurityUser.class));
    }
    @Test
    public void getRatings_shouldReturnListOfRatings() throws Exception {
        Long gameId = 1L;
        RatingDto rating1 = new RatingDto();
        rating1.setId(1L);
        rating1.setRating(4);
        rating1.setComment("Good game");
        RatingDto rating2 = new RatingDto();
        rating1.setId(2L);
        rating1.setRating(5);
        rating1.setComment("Ok game");
        List<RatingDto> ratings = Arrays.asList(rating1, rating2);

        given(ratingService.getRatings(gameId, Optional.empty())).willReturn(ratings);

        mockMvc.perform(get("/games/{id}/rate", gameId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(rating1.getId()))
                .andExpect(jsonPath("$[0].rating").value(rating1.getRating()))
                .andExpect(jsonPath("$[0].comment").value(rating1.getComment()))
                .andExpect(jsonPath("$[1].id").value(rating2.getId()))
                .andExpect(jsonPath("$[1].rating").value(rating2.getRating()))
                .andExpect(jsonPath("$[1].comment").value(rating2.getComment()));

        verify(ratingService).getRatings(gameId, Optional.empty());
    }

}