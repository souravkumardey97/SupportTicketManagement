package com.supportticket.service;

import com.supportticket.api.dto.LoginRequest;
import com.supportticket.api.dto.LoginResponse;
import com.supportticket.persistence.UserRepository;
import com.supportticket.persistence.entity.UserEntity;
import com.supportticket.exception.BadRequestException;
import com.supportticket.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (AuthenticationException ex) {
            throw new BadRequestException("Invalid username or password");
        }
        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user missing from database"));
        String token = jwtService.generateToken(user);
        return new LoginResponse(token, "Bearer", user.getRole());
    }
}
