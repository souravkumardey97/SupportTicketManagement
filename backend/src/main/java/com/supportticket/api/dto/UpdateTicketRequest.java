package com.supportticket.api.dto;

import com.supportticket.domain.TicketPriority;
import jakarta.validation.constraints.Size;

public record UpdateTicketRequest(
        @Size(max = 200) String title,
        @Size(max = 5000) String description,
        TicketPriority priority,
        Long assigneeId
) {
}
