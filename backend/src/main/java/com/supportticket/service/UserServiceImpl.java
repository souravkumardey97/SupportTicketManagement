package com.supportticket.service;

import com.supportticket.api.dto.CreateUserRequest;
import com.supportticket.api.dto.UserSummaryResponse;
import com.supportticket.domain.Role;
import com.supportticket.exception.BadRequestException;
import com.supportticket.persistence.UserRepository;
import com.supportticket.persistence.entity.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserSummaryResponse> listUsers(Role role) {
        List<com.supportticket.persistence.entity.UserEntity> users =
                role == null ? userRepository.findAll() : userRepository.findByRole(role);
        return users.stream()
                .map(TicketMapper::toUserSummary)
                .toList();
    }

    @Override
    @Transactional
    public UserSummaryResponse createUser(CreateUserRequest request) {
        String username = request.username().trim();
        if (!StringUtils.hasText(username)) {
            throw new BadRequestException("Username must not be null or empty");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        return TicketMapper.toUserSummary(userRepository.save(user));
    }
}
