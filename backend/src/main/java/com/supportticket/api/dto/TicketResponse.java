package com.supportticket.api.dto;

import com.supportticket.domain.TicketPriority;
import com.supportticket.domain.TicketStatus;

import java.time.Instant;

public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketPriority priority,
        TicketStatus status,
        UserSummaryResponse assignee,
        Instant createdAt,
        Instant updatedAt
) {
}
