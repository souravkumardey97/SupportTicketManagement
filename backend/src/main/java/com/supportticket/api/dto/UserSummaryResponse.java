package com.supportticket.api.dto;

import com.supportticket.domain.Role;

public record UserSummaryResponse(
        Long id,
        String username,
        Role role
) {
}
