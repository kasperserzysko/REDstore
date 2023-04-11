package com.kasperserzysko.client_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasperserzysko.contracts.user_dtos.UserDetailsDto;
import com.kasperserzysko.data.models.enums.Role;
import com.kasperserzysko.security.services.interfaces.IAuthenticationService;
import com.kasperserzysko.tools.exceptions.FoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAuthenticationService authenticationService;

    @Test
    public void registerUser_should_returnsOk() throws Exception {
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setEmail("test@example.com");
        userDetailsDto.setPassword("password");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetailsDto)))
                .andExpect(status().isOk());
    }
    @Test
    public void registerUser_should_returnsBadRequest() throws Exception {
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setEmail("test@example.com");
        userDetailsDto.setPassword("password");

        willThrow(FoundException.class).given(authenticationService).registerUser(userDetailsDto, Role.ROLE_USER);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetailsDto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void loginUser_should_returnsOk() throws Exception {
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setEmail("test@example.com");
        userDetailsDto.setPassword("password");

        given(authenticationService.login(userDetailsDto)).willReturn("token");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetailsDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("token"));
    }
    @Test
    public void loginUser_should_returnsNotFound() throws Exception {
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setEmail("test@example.com");
        userDetailsDto.setPassword("password");

        willThrow(UsernameNotFoundException.class).given(authenticationService).login(userDetailsDto);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetailsDto)))
                .andExpect(status().isNotFound());
    }
    @Test
    public void activateAccount_should_returnsOk() throws Exception {
        String activationLink = "abc123";

        mockMvc.perform(post("/activate/" + activationLink))
                .andExpect(status().isOk());
    }

}