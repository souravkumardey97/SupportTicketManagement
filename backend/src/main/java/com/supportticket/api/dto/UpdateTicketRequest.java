package com.supportticket.api.dto;

import com.supportticket.domain.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTicketRequest(
        @NotBlank(message = "Title must not be null or empty")
        @Size(max = 200)
        String title,
        @Size(max = 5000) String description,
        @NotNull(message = "Priority must not be null")
        TicketPriority priority,
        @NotNull(message = "Assignee must not be null")
        Long assigneeId
) {
}
