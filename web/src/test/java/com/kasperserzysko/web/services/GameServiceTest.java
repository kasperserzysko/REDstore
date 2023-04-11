package com.kasperserzysko.web.services;

import com.kasperserzysko.contracts.game_dtos.GameDetailsDto;
import com.kasperserzysko.data.models.Game;
import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Tag;
import com.kasperserzysko.data.repositories.GameRepository;
import com.kasperserzysko.data.repositories.RatingRepository;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.tools.mappers.IMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Slf4j
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

    @TempDir
    Path tempDir;

    @Test
    void should_SaveGame() throws IOException {
        GameDetailsDto dto = new GameDetailsDto();
        dto.setTitle("Test Game");
        dto.setDescription("A game for testing purposes");
        dto.setGenres(new HashSet<>(List.of(Genre.ACTION)));
        dto.setTags(new HashSet<>(List.of(Tag.FANTASY)));
        dto.setReleaseDate("12.03.2023");
        MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", new byte[] {});

        Game savedGame = new Game();

        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

        gameService.createGame(dto, image);
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void should_ThrowConstraintViolationException() throws IOException {
        var dto = new GameDetailsDto();
        MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", new byte[] {});

        assertThrows(ConstraintViolationException.class, () -> gameService.createGame(dto, image));
    }

    @Test
    void should_DeleteGame() throws NotFoundException, IOException {
        Long gameId = 1L;
        Game gameEntity = new Game();
        gameEntity.setId(gameId);
        gameEntity.setTitle("Test Game");

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        gameService.deleteGame(gameId);

        verify(gameRepository).delete(gameEntity);
    }
    @Test
    void should_ThrowNotFoundException_deleteGame() {
        Long gameId = 1L;
        Game gameEntity = new Game();
        gameEntity.setId(gameId);
        gameEntity.setTitle("Test Game");

        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gameService.deleteGame(gameId));
        verify(gameRepository, never()).delete(any(Game.class));
    }
    @Test
    void should_GetGame() throws NotFoundException {

        Game game = new Game();
        game.setTitle("testGame");
        game.setReleaseDate(LocalDate.of(2021,3,14));
        when(gameRepository.findById(anyLong())).thenReturn(Optional.of(game));

        GameDetailsDto actualGameDetailsDto = gameService.getGame(2L);

        assertNotNull(actualGameDetailsDto);
        assertEquals(game.getTitle(), actualGameDetailsDto.getTitle());
        verify(gameRepository, times(1)).findById(anyLong());
    }



    @Test
    void should_ThrowNotFoundException_getGame() {
        when(gameRepository.findById(anyLong())).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> gameService.getGame(2L));
        verify(gameRepository, times(1)).findById(2L);
    }


}