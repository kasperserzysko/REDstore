package com.kasperserzysko.security.services;

import com.kasperserzysko.contracts.user_dtos.UserCredentialsDto;
import com.kasperserzysko.data.models.Token;
import com.kasperserzysko.data.models.User;
import com.kasperserzysko.data.models.enums.Role;
import com.kasperserzysko.data.repositories.TokenRepository;
import com.kasperserzysko.data.repositories.UserRepository;
import com.kasperserzysko.security.models.SecurityUser;
import com.kasperserzysko.security.services.interfaces.IAuthenticationService;
import com.kasperserzysko.tools.mappers.IMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Setter
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IMapper mapper;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    public void registerUser(UserCredentialsDto dto, Role role) {
        var userEntity = new User();

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        mapper.getUserMapper()
                .mapToEntity
                .accept(dto, userEntity);
        switch (role){
            case ROLE_ADMIN -> {
                userEntity.getRoles().addAll(List.of(Role.ROLE_USER, Role.ROLE_ADMIN));
            }
            case ROLE_USER -> {
                userEntity.getRoles().add(Role.ROLE_USER);
            }
        }
        userRepository.save(userEntity);
    }

    @Override
    public String login(UserCredentialsDto dto) throws AuthenticationException {
        var userEntity = userRepository.findUserByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email not found!"));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        var jwtToken = jwtService.generateToken(new SecurityUser(userEntity));
        saveUserToken(userEntity, jwtToken);
        return jwtToken;
    }


    private void saveUserToken(User user, String jwtToken) {
        var token = new Token();
        token.setToken(jwtToken);
        token.setUser(user);
        token.setExpired(false);
        token.setRevoked(false);
        tokenRepository.save(token);
    }
}
