package com.supportticket.api.dto;

import java.time.Instant;

public record CommentResponse(
        Long id,
        String body,
        UserSummaryResponse author,
        Instant createdAt
) {
}
