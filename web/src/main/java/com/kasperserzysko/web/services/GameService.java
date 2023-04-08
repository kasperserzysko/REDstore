package com.kasperserzysko.web.services;

import com.kasperserzysko.contracts.game_dtos.GameCredentialsDto;
import com.kasperserzysko.contracts.game_dtos.GameDto;
import com.kasperserzysko.data.models.Game;
import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Tag;
import com.kasperserzysko.data.repositories.GameRepository;
import com.kasperserzysko.data.repositories.specifications.GameSpecification;
import com.kasperserzysko.tools.FileService;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.tools.mappers.IMapper;
import com.kasperserzysko.web.services.interfaces.IGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GameService implements IGameService {

    private final IMapper mapper;

    private final GameRepository gameRepository;

    @Override
    public void createGame(GameCredentialsDto dto, MultipartFile[] images, MultipartFile titleImage) throws IOException {
        Game gameEntity = new Game();

        mapper.getGameMapper()
                .mapToEntity.accept(dto, gameEntity);
        gameRepository.save(gameEntity);

        FileService.saveTitleImage(titleImage, gameEntity.getId());
        for (MultipartFile image : images) {
            FileService.saveImage(image, gameEntity.getId());
        }
    }

    @Override
    public void deleteGame(Long id) {

    }

    @Override
    public GameCredentialsDto getGame(Long id) throws NotFoundException {
        return mapper
                .getGameMapper()
                .mapToCredentials
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
        final Pageable gamesPage = page.map(p -> PageRequest.of(p, ITEMS_PER_PAGE, Sort.by(sort(sort, direction))))
                                    .orElseGet(() ->  PageRequest.of(0, ITEMS_PER_PAGE, Sort.by(sort(sort, direction))));

        return gameRepository
                .findAll(Specification.allOf(gameSpecifications), gamesPage)
                .getContent()
                .stream()
                .map(mapper
                        .getGameMapper()
                        .mapToDto)
                .toList();
    }

    @Override
    public void updateGame(GameCredentialsDto dto, Long gameId) throws IOException, NotFoundException {
        var gameEntity = gameRepository
                .findById(gameId)
                .orElseThrow(() -> new NotFoundException("Couldn't find game iwth id: " + gameId));
        mapper.getGameMapper().mapToEntity.accept(dto, gameEntity);
        gameRepository.save(gameEntity);
    }

    @Override
    public GameCredentialsDto getGameUpdateCredentials(Long gameId) throws NotFoundException {
        return mapper
                .getGameMapper()
                .mapToCredentials
                .apply(gameRepository
                .findById(gameId)
                .orElseThrow(() -> new NotFoundException("Couldn't find game iwth id: " + gameId))
                );
    }

    private List<Sort.Order> sort(Optional<String> sort, Optional<String> direction){
        List<Sort.Order> orders = new ArrayList<>();
        direction.ifPresent(d -> {
            if (d.equals("asc")){
                sort.ifPresent(s ->{
                    switch (s) {
                        case "date" -> orders.add(Sort.Order.asc("date"));
                        case "price" -> orders.add(Sort.Order.asc("price"));
                        default -> orders.add(Sort.Order.asc("id"));
                    }
                } );
            }else {
                sort.ifPresent(s ->{
                    switch (s) {
                        case "date" -> orders.add(Sort.Order.desc("date"));
                        case "price" -> orders.add(Sort.Order.desc("price"));
                        default -> orders.add(Sort.Order.desc("id"));
                    }
                } );
            }
        });
        return orders;
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
}
