package com.supportticket.service;

import com.supportticket.api.dto.CommentResponse;
import com.supportticket.api.dto.TicketResponse;
import com.supportticket.api.dto.UserSummaryResponse;
import com.supportticket.persistence.entity.CommentEntity;
import com.supportticket.persistence.entity.TicketEntity;
import com.supportticket.persistence.entity.UserEntity;

final class TicketMapper {

    private TicketMapper() {
    }

    static TicketResponse toResponse(TicketEntity ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus(),
                toUserSummary(ticket.getAssignee()),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt());
    }

    static CommentResponse toCommentResponse(CommentEntity comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getBody(),
                toUserSummary(comment.getAuthor()),
                comment.getCreatedAt());
    }

    static UserSummaryResponse toUserSummary(UserEntity user) {
        return new UserSummaryResponse(user.getId(), user.getUsername(), user.getRole());
    }
}
