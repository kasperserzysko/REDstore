package com.kasperserzysko.web.services.interfaces;

import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;
import com.kasperserzysko.contracts.rating_dtos.RatingDto;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.tools.exceptions.PermissionDeniedException;

import java.util.List;
import java.util.Optional;

public interface IRatingService {
    List<RatingDto> getRatings(Long gameId, Optional<Integer> page);
    void updateRating(RatingDetailsDto dto, Long ratingId, SecurityUser loggedUser) throws NotFoundException, PermissionDeniedException;
    RatingDetailsDto getUpdateRatingCredentials(Long ratingId, SecurityUser loggedUser) throws NotFoundException, PermissionDeniedException;
    void deleteRating(Long ratingId, SecurityUser loggedUser) throws NotFoundException, PermissionDeniedException;
}
