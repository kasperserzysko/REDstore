package com.kasperserzysko.web.services.interfaces;

import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.tools.exceptions.FoundException;
import com.kasperserzysko.tools.exceptions.NotFoundException;

public interface IUserService {

    void rateGame(RatingDetailsDto dto, Long gameId, SecurityUser loggedUser) throws NotFoundException, FoundException;
}
