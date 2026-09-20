package com.supportticket.service;

import com.supportticket.api.dto.CommentResponse;
import com.supportticket.api.dto.CreateCommentRequest;
import com.supportticket.exception.ResourceNotFoundException;
import com.supportticket.persistence.CommentRepository;
import com.supportticket.persistence.TicketRepository;
import com.supportticket.persistence.UserRepository;
import com.supportticket.persistence.entity.CommentEntity;
import com.supportticket.persistence.entity.TicketEntity;
import com.supportticket.persistence.entity.UserEntity;
import com.supportticket.security.DatabaseUserPrincipal;
import com.supportticket.security.JwtUserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CommentResponse addComment(Long ticketId, CreateCommentRequest request) {
        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        UserEntity author = resolveCurrentUser();

        CommentEntity comment = new CommentEntity();
        comment.setTicket(ticket);
        comment.setAuthor(author);
        comment.setBody(request.body().trim());
        return TicketMapper.toCommentResponse(commentRepository.save(comment));
    }

    private UserEntity resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtUserPrincipal jwtUser) {
            username = jwtUser.getUsername();
        } else if (principal instanceof DatabaseUserPrincipal databaseUser) {
            username = databaseUser.getUsername();
        } else {
            username = authentication.getName();
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
