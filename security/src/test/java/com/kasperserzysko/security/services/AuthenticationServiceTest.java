package com.kasperserzysko.security.services;

import com.kasperserzysko.contracts.user_dtos.UserDetailsDto;
import com.kasperserzysko.data.models.User;
import com.kasperserzysko.data.models.enums.Role;
import com.kasperserzysko.data.repositories.TokenRepository;
import com.kasperserzysko.data.repositories.UserRepository;
import com.kasperserzysko.email_service.services.EmailService;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.tools.exceptions.FoundException;
import com.kasperserzysko.tools.mappers.IMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthenticationServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IMapper mapper;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp(){
        authenticationService = new AuthenticationService(passwordEncoder, authenticationManager, jwtService, mapper, emailService, userRepository, tokenRepository);
    }

    @Test
    public void testRegisterUser_Success() throws FoundException {
        UserDetailsDto dto = new UserDetailsDto();
        dto.setEmail("test@example.com");
        dto.setPassword(passwordEncoder.encode("password"));

        User userEntity = new User();

        when(userRepository.findUserByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        authenticationService.registerUser(dto, Role.ROLE_USER);

        verify(userRepository, times(1)).findUserByEmail(dto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    public void testRegisterUser_FoundException() {
        UserDetailsDto dto = new UserDetailsDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password");

        when(userRepository.findUserByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));

        Assertions.assertThrows(FoundException.class, () -> authenticationService.registerUser(dto, Role.ROLE_USER));
    }
}


