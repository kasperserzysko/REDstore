package com.kasperserzysko.web.services.interfaces;

import com.kasperserzysko.contracts.game_dtos.GameCredentialsDto;
import com.kasperserzysko.contracts.game_dtos.GameDto;
import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Tag;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IGameService {

    void createGame(GameCredentialsDto dto, MultipartFile[] images, MultipartFile titleImage) throws IOException;
    void deleteGame(Long id);
    GameCredentialsDto getGame(Long id) throws NotFoundException;
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
    void updateGame(GameCredentialsDto dto, Long gameId) throws IOException, NotFoundException;
    GameCredentialsDto getGameUpdateCredentials(Long gameId) throws NotFoundException;
}
