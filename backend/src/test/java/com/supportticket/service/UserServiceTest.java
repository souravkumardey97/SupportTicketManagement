package com.supportticket.service;

import com.supportticket.api.dto.CreateUserRequest;
import com.supportticket.domain.Role;
import com.supportticket.exception.BadRequestException;
import com.supportticket.persistence.UserRepository;
import com.supportticket.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserHashesPasswordAndPersists() {
        when(userRepository.findByUsername("newagent")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret-pass")).thenReturn("hashed");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        var response = userService.createUser(
                new CreateUserRequest("newagent", "secret-pass", Role.USER));

        assertThat(response.username()).isEqualTo("newagent");
        assertThat(response.role()).isEqualTo(Role.USER);
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("hashed");
    }

    @Test
    void createUserRejectsDuplicateUsername() {
        UserEntity existing = new UserEntity();
        existing.setUsername("taken");
        when(userRepository.findByUsername("taken")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> userService.createUser(
                new CreateUserRequest("taken", "secret-pass", Role.USER)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Username already exists");
    }
}
