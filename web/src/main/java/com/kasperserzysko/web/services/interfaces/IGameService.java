package com.kasperserzysko.web.services.interfaces;

import com.kasperserzysko.contracts.game_dtos.GameDetailsDto;
import com.kasperserzysko.contracts.game_dtos.GameDto;
import com.kasperserzysko.contracts.game_dtos.GameRatingDto;
import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Tag;
import com.kasperserzysko.tools.FileService;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IGameService {

    void createGame(GameDetailsDto dto, MultipartFile titleImage) throws IOException;
    void deleteGame(Long id) throws NotFoundException, IOException;
    GameDetailsDto getGame(Long id) throws NotFoundException;
    List<GameDto> getGames(Optional<Float> priceMax,
                           Optional<Float> priceMin,
                           Optional<String> title,
                           Optional<String> dateMax,
                           Optional<String> dateMin,
                           Optional<Tag[]> tags,
                           Optional<Genre[]> genres,
                           Optional<Integer> page,
                           Optional<String> sort,
                           Optional<String> direction);
    void updateGame(GameDetailsDto dto, Long gameId) throws IOException, NotFoundException;
    GameDetailsDto getGameUpdateCredentials(Long gameId) throws NotFoundException;
    GameRatingDto getGameRating(Long gameId) throws NotFoundException;
    byte[] getImage(Long gameId) throws IOException;

}
