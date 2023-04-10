package com.kasperserzysko.web.services;

import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;
import com.kasperserzysko.contracts.rating_dtos.RatingDto;
import com.kasperserzysko.data.repositories.RatingRepository;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.tools.exceptions.PermissionDeniedException;
import com.kasperserzysko.tools.mappers.IMapper;
import com.kasperserzysko.web.services.interfaces.IRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService implements IRatingService {

    private final RatingRepository ratingRepository;
    private final IMapper mapper;

    @Override
    public List<RatingDto> getRatings(Long gameId, Optional<Integer> page){

        final int ITEMS_PER_PAGE = 10;
        final Pageable ratingsPage = page.map(p -> PageRequest.of(p, ITEMS_PER_PAGE))
                .orElseGet(() ->  PageRequest.of(0, ITEMS_PER_PAGE));

        return ratingRepository
                .getRatings(gameId, ratingsPage).stream()
                .map(mapper.getRatingMapper().mapToDto)
                .toList();
    }

    @Override
    public void updateRating(RatingDetailsDto dto, Long ratingId, SecurityUser loggedUser) throws NotFoundException, PermissionDeniedException {
        var ratingEntity = ratingRepository
                .findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Couldn't find rating with id: " + ratingId));
        var userEntity = loggedUser.user();
        if (!ratingEntity.getUser().getId().equals(userEntity.getId())){
            throw new PermissionDeniedException("You don't have access to edit this rating");
        }
        mapper.getRatingMapper().mapToEntity.accept(dto, ratingEntity);

        ratingRepository.save(ratingEntity);
    }

    @Override
    public RatingDetailsDto getUpdateRatingCredentials(Long ratingId, SecurityUser loggedUser) throws NotFoundException, PermissionDeniedException {
        var ratingEntity = ratingRepository
                .findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Couldn't find rating with id: " + ratingId));
        var userEntity = loggedUser.user();
        if (!ratingEntity.getUser().getId().equals(userEntity.getId())){
            throw new PermissionDeniedException("You don't have access to edit this rating");
        }
        return mapper
                .getRatingMapper()
                .mapToDetails
                .apply(ratingEntity);
    }

    @Override
    public void deleteRating(Long ratingId, SecurityUser loggedUser) throws NotFoundException, PermissionDeniedException{
        var ratingEntity = ratingRepository
                .findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Couldn't find rating with id: " + ratingId));
        var userEntity = loggedUser.user();
        if (!ratingEntity.getUser().getId().equals(userEntity.getId())){
            throw new PermissionDeniedException("You don't have access to edit this rating");
        }
        ratingEntity.getUser().getRatings().remove(ratingEntity);
        ratingEntity.setUser(null);
        ratingEntity.getGame().getRatings().remove(ratingEntity);
        ratingEntity.setGame(null);

        ratingRepository.delete(ratingEntity);
    }
}
