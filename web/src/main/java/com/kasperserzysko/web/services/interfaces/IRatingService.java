package com.kasperserzysko.web.services.interfaces;

import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;

import java.util.List;
import java.util.Optional;

public interface IRatingService {
    List<RatingDetailsDto> getRatings(Long gameId, Optional<Integer> page);
}
