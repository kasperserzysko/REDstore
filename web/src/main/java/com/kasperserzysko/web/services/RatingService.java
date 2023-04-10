package com.kasperserzysko.web.services;

import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;
import com.kasperserzysko.data.repositories.RatingRepository;
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

    public List<RatingDetailsDto> getRatings(Long gameId, Optional<Integer> page){

        final int ITEMS_PER_PAGE = 10;
        final Pageable ratingsPage = page.map(p -> PageRequest.of(p, ITEMS_PER_PAGE))
                .orElseGet(() ->  PageRequest.of(0, ITEMS_PER_PAGE));

        return ratingRepository
                .getRatings(gameId, ratingsPage).stream()
                .map(mapper.getRatingMapper().mapToDetails)
                .toList();
    }

}
