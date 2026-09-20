package com.supportticket.api.dto;

import com.supportticket.domain.Role;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Role role
) {
}
