package com.kasperserzysko.client_app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasperserzysko.contracts.rating_dtos.RatingDetailsDto;
import com.kasperserzysko.data.models.User;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.security.services.interfaces.IAuthenticationService;
import com.kasperserzysko.tools.exceptions.NotFoundException;
import com.kasperserzysko.tools.exceptions.PermissionDeniedException;
import com.kasperserzysko.web.services.interfaces.IRatingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IRatingService ratingService;


    @Test
    @WithMockUser
    public void getUpdateRating_should_returnOk_withJson() throws Exception {
        Long ratingId = 1L;
        RatingDetailsDto ratingDetailsDto = new RatingDetailsDto();
        when(ratingService.getUpdateRatingCredentials(eq(ratingId), any())).thenReturn(ratingDetailsDto);

        mockMvc.perform(get("/ratings/{id}", ratingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value(ratingDetailsDto.getComment()))
                .andExpect(jsonPath("$.rating").value(ratingDetailsDto.getRating()));

        verify(ratingService, times(1)).getUpdateRatingCredentials(eq(ratingId), any());
    }

    @Test
    public void getUpdateRating_should_returnForbidden() throws Exception {
        mockMvc.perform(get("/ratings/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testGetUpdateRating_NotFound() throws Exception {
        Long ratingId = 1L;
        when(ratingService.getUpdateRatingCredentials(eq(ratingId), any()))
                .thenThrow(new NotFoundException("Rating not found"));

        mockMvc.perform(get("/ratings/{id}", ratingId))
                .andExpect(status().isNotFound());


        verify(ratingService, times(1)).getUpdateRatingCredentials(eq(ratingId), any());
    }

    @Test
    @WithMockUser
    public void testGetUpdateRating_PermissionDenied() throws Exception {
        Long ratingId = 1L;
        when(ratingService.getUpdateRatingCredentials(eq(ratingId), any()))
                .thenThrow(new PermissionDeniedException("Permission denied"));

        mockMvc.perform(get("/ratings/{id}", ratingId))
                .andExpect(status().isForbidden());

        verify(ratingService, times(1)).getUpdateRatingCredentials(eq(ratingId), any());
    }

    @Test
    public void testUpdateRating() throws Exception {
        Long ratingId = 1L;
        RatingDetailsDto dto = new RatingDetailsDto();
        SecurityUser loggedUser = new SecurityUser(new User());

        mockMvc.perform(put("/ratings/" + ratingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .with(user(loggedUser)))
                .andExpect(status().isOk());

        verify(ratingService, times(1)).updateRating(dto, ratingId, loggedUser);
    }

    @Test
    public void updateRating_invalidRatingId_throwNotFoundException() throws Exception {
        Long ratingId = 1L;
        RatingDetailsDto ratingDetailsDto = new RatingDetailsDto();
        SecurityUser loggedUser = new SecurityUser(new User());
        doThrow(new NotFoundException("Couldn't find rating with id: " + ratingId))
                .when(ratingService).updateRating(ratingDetailsDto, ratingId, loggedUser);

        mockMvc.perform(put("/ratings/" + ratingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(ratingDetailsDto))
                        .with(user(loggedUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateRating_differentSecurityUser_throwPermissionDeniedException() throws Exception {
        Long ratingId = 1L;
        RatingDetailsDto ratingDetailsDto = new RatingDetailsDto();
        SecurityUser loggedUser = new SecurityUser(new User());
        doThrow(new PermissionDeniedException("You don't have access to edit this rating"))
                .when(ratingService).updateRating(ratingDetailsDto, ratingId, loggedUser);

        mockMvc.perform(put("/ratings/" + ratingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(ratingDetailsDto))
                        .with(user(loggedUser)))
                .andExpect(status().isForbidden());
    }
}