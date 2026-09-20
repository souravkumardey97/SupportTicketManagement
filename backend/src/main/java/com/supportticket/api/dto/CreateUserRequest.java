package com.supportticket.api.dto;

import com.supportticket.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Username must not be null or empty")
        @Size(max = 100)
        String username,
        @NotBlank(message = "Password must not be null or empty")
        @Size(min = 8, max = 128)
        String password,
        @NotNull(message = "Role must not be null")
        Role role
) {
}
