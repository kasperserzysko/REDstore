package com.kasperserzysko.web.services;

import com.kasperserzysko.contracts.game_dtos.GameDetailsDto;
import com.kasperserzysko.data.models.Game;
import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Tag;
import com.kasperserzysko.data.repositories.GameRepository;
import com.kasperserzysko.data.repositories.RatingRepository;
import com.kasperserzysko.tools.mappers.IMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class GameServiceTest {

    @Autowired
    private IMapper mapper;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private RatingRepository ratingRepository;

    private GameService gameService;

    @BeforeEach
    void setUp(){
     gameService = new GameService(mapper, gameRepository, ratingRepository);
    }


    @Test
    void createGame_ShouldSaveGameAndImage() throws IOException {
        // Arrange
        GameDetailsDto dto = new GameDetailsDto();
        dto.setTitle("Test Game");
        dto.setDescription("A game for testing purposes");
        dto.setGenres(new HashSet<>(List.of(Genre.ACTION)));
        dto.setTags(new HashSet<>(List.of(Tag.FANTASY)));
        dto.setReleaseDate("12.03.2023");
        MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", new byte[] {});

        // Create a new Game object to return when save() is called on the gameRepository
        Game savedGame = new Game();
        savedGame.setId(1L);
        savedGame.setTitle(dto.getTitle());
        savedGame.setDescription(dto.getDescription());

        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

        // Act
        gameService.createGame(dto, image);

        // Assert
        verify(gameRepository, times(1)).save(any(Game.class));
    }
}