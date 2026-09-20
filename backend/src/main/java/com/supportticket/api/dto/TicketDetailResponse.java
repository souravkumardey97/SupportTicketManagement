package com.supportticket.api.dto;

import java.util.List;

public record TicketDetailResponse(
        TicketResponse ticket,
        List<CommentResponse> comments
) {
}
