package com.kasperserzysko.web.services;

import com.kasperserzysko.contracts.game_dtos.GameDetailsDto;
import com.kasperserzysko.contracts.game_dtos.GameDto;
import com.kasperserzysko.contracts.game_dtos.GameRatingDto;
import com.kasperserzysko.data.models.Game;
import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Tag;
import com.kasperserzysko.data.repositories.GameRepository;
import com.kasperserzysko.data.repositories.RatingRepository;
import com.kasperserzysko.data.repositories.specifications.GameSpecification;
import com.kasperserzysko.tools.FileService;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.tools.mappers.IMapper;
import com.kasperserzysko.web.services.interfaces.IGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GameService implements IGameService {

    private final IMapper mapper;

    private final GameRepository gameRepository;
    private final RatingRepository ratingRepository;

    @Override
    public void createGame(GameDetailsDto dto, MultipartFile image) throws IOException {
        var gameEntity = new Game();

        mapper.getGameMapper()
                .mapToEntity.accept(dto, gameEntity);
        gameRepository.save(gameEntity);

        FileService.saveImage(image, gameEntity.getId());
    }

    @CacheEvict(value = "gameCredentialsCache", key = "#id")
    @Override
    public void deleteGame(Long id) throws NotFoundException, IOException {
        var gameEntity = gameRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Couldn't find game with id: " + id));
        handleDelete(gameEntity);
        gameRepository.delete(gameEntity);
    }

    @Override
    public byte[] getImage(Long gameId) throws IOException {
        return FileService.getImage(gameId);
    }

    @Cacheable(value = "gameCredentialsCache", key = "#id")
    @Override
    public GameDetailsDto getGame(Long id) throws NotFoundException {
        log.info("GAME FROM DB");
        return mapper
                .getGameMapper()
                .mapToDetails
                .apply(gameRepository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException("Couldn't find game with id: " + id))
                );
    }


    @Override
    public List<GameDto> getGames(Optional<Float> priceMax,
                                  Optional<Float> priceMin,
                                  Optional<String> title,
                                  Optional<String> dateMax,
                                  Optional<String> dateMin,
                                  Optional<Tag[]> tags,
                                  Optional<Genre[]> genres,
                                  Optional<Integer> page,
                                  Optional<String> sort,
                                  Optional<String> direction) {
        List<Specification<Game>> gameSpecifications = new ArrayList<>();
        addSpecifications(priceMax, priceMin, title, dateMax, dateMin, tags, genres, gameSpecifications);
        final int ITEMS_PER_PAGE = 10;
        final Pageable gamesPage = page.map(p -> PageRequest.of(p, ITEMS_PER_PAGE,sort(sort, direction)))
                                    .orElseGet(() ->  PageRequest.of(0, ITEMS_PER_PAGE, sort(sort, direction)));
        return gameRepository
                .findAll(Specification.allOf(gameSpecifications), gamesPage)
                .getContent()
                .stream()
                .map(mapper
                        .getGameMapper()
                        .mapToDto)
                .toList();
    }

    @CachePut(value = "gameCredentialsCache", key = "#gameId")
    @Override
    public void updateGame(GameDetailsDto dto, Long gameId) throws NotFoundException {
        var gameEntity = gameRepository
                .findById(gameId)
                .orElseThrow(() -> new NotFoundException("Couldn't find game with id: " + gameId));
        mapper.getGameMapper().mapToEntity.accept(dto, gameEntity);
        gameRepository.save(gameEntity);
    }

    @Override
    public GameDetailsDto getGameUpdateCredentials(Long gameId) throws NotFoundException {
        return mapper
                .getGameMapper()
                .mapToDetails
                .apply(gameRepository
                    .findById(gameId)
                    .orElseThrow(() -> new NotFoundException("Couldn't find game with id: " + gameId))
                );
    }

    @Override
    public GameRatingDto getGameRating(Long gameId) throws NotFoundException {
        gameRepository
                .findById(gameId)
                .orElseThrow(() -> new NotFoundException("Couldn't find game with id: " + gameId));
        return new GameRatingDto(ratingRepository.getRatingAvg(gameId));
    }
    private Sort sort(Optional<String> sort, Optional<String> direction){
        Sort.Direction dir = Sort.Direction.DESC;
        String sortBy = "releaseDate";
        if (direction.isPresent()){
            switch (direction.get().toLowerCase()) {
                case "asc" -> dir = Sort.Direction.ASC;
                case "desc" -> dir = Sort.Direction.DESC;
            }
        }
        if (sort.isPresent()){
            switch (sort.get().toLowerCase()){
                case "title" -> sortBy = "title";
                case "price" -> sortBy = "price";
            }
        }
        return Sort.by(dir, sortBy);
    }

    private void addSpecifications(Optional<Float> priceMax,
                                   Optional<Float> priceMin,
                                   Optional<String> title,
                                   Optional<String> dateMax,
                                   Optional<String> dateMin,
                                   Optional<Tag[]> tags,
                                   Optional<Genre[]> genres,
                                   List<Specification<Game>> gameSpecifications){
        priceMax.ifPresent(p -> gameSpecifications
                .add(GameSpecification.priceLessOrEqualsThan(p)));
        priceMin.ifPresent(p -> gameSpecifications
                .add(GameSpecification.priceMoreOrEqualsThan(p)));
        title.ifPresent(t -> gameSpecifications
                .add(GameSpecification.titleLike(t)));
        dateMax.ifPresent(d -> gameSpecifications
                .add(GameSpecification.releaseBefore(d)));
        dateMin.ifPresent(d -> gameSpecifications
                .add(GameSpecification.releaseAfter(d)));
        tags.ifPresent(t -> gameSpecifications
                .add(GameSpecification.inTags(t)));
        genres.ifPresent(g -> gameSpecifications
                .add(GameSpecification.inGenres(g)));
    }

    private void handleDelete(Game gameEntity) throws IOException {
        FileService.deleteFolder(gameEntity.getId());
        for (int index = 0; index < gameEntity.getRatings().size(); index++){
            var ratingEntity = gameEntity.getRatings().get(index);
            var ratingUser = ratingEntity.getUser();

            ratingUser.getRatings().remove(ratingEntity);
            ratingEntity.setUser(null);
            ratingEntity.setGame(null);
            gameEntity.getRatings().remove(ratingEntity);
            ratingRepository.delete(ratingEntity);
        }
    }
}
