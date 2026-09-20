package com.supportticket.service;

import com.supportticket.api.dto.CreateTicketRequest;
import com.supportticket.api.dto.TicketDetailResponse;
import com.supportticket.api.dto.TicketPageResponse;
import com.supportticket.api.dto.TicketResponse;
import com.supportticket.api.dto.UpdateTicketRequest;
import com.supportticket.domain.Role;
import com.supportticket.domain.TicketPriority;
import com.supportticket.domain.TicketStatus;
import com.supportticket.domain.TicketStatusTransitionService;
import com.supportticket.exception.BadRequestException;
import com.supportticket.exception.ResourceNotFoundException;
import com.supportticket.persistence.CommentRepository;
import com.supportticket.persistence.TicketRepository;
import com.supportticket.persistence.UserRepository;
import com.supportticket.persistence.entity.TicketEntity;
import com.supportticket.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TicketStatusTransitionService transitionService;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            CommentRepository commentRepository,
            TicketStatusTransitionService transitionService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.transitionService = transitionService;
    }

    @Override
    @Transactional(readOnly = true)
    public TicketPageResponse listTickets(String keyword, TicketStatus status, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        Page<TicketEntity> result = listTicketPage(normalizedKeyword, status, pageable);
        List<TicketResponse> content = result.getContent().stream()
                .map(TicketMapper::toResponse)
                .toList();
        return new TicketPageResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    private Page<TicketEntity> listTicketPage(String keyword, TicketStatus status, Pageable pageable) {
        if (keyword == null && status == null) {
            return ticketRepository.findAll(pageable);
        }
        if (keyword != null && status != null) {
            return ticketRepository.searchByStatusAndKeyword(status, keyword, pageable);
        }
        if (status != null) {
            return ticketRepository.findByStatus(status, pageable);
        }
        return ticketRepository.searchByKeyword(keyword, pageable);
    }

    @Override
    public TicketResponse createTicket(CreateTicketRequest request) {
        UserEntity assignee = resolveAssignee(request.assigneeId());
        TicketEntity ticket = new TicketEntity();
        ticket.setTitle(request.title().trim());
        ticket.setDescription(request.description().trim());
        ticket.setPriority(request.priority() != null ? request.priority() : TicketPriority.P1);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAssignee(assignee);
        return TicketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDetailResponse getTicket(Long ticketId) {
        TicketEntity ticket = findTicket(ticketId);
        List<com.supportticket.api.dto.CommentResponse> comments = commentRepository
                .findByTicket_IdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(TicketMapper::toCommentResponse)
                .toList();
        return new TicketDetailResponse(TicketMapper.toResponse(ticket), comments);
    }

    @Override
    public TicketResponse updateTicket(Long ticketId, UpdateTicketRequest request) {
        TicketEntity ticket = findTicket(ticketId);
        ticket.setTitle(request.title().trim());
        if (request.description() != null) {
            ticket.setDescription(request.description().trim());
        }
        ticket.setPriority(request.priority());
        ticket.setAssignee(resolveAssignee(request.assigneeId()));
        return TicketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse updateTicketStatus(Long ticketId, TicketStatus status) {
        TicketEntity ticket = findTicket(ticketId);
        TicketStatus newStatus = transitionService.transition(ticket.getStatus(), status);
        ticket.setStatus(newStatus);
        return TicketMapper.toResponse(ticketRepository.save(ticket));
    }

    private TicketEntity findTicket(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }

    private UserEntity resolveAssignee(Long assigneeId) {
        UserEntity assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        if (assignee.getRole() != Role.USER) {
            throw new BadRequestException("Assignee must be a user with USER role");
        }
        return assignee;
    }

    private Sort parseSort(String sort) {
        if (!StringUtils.hasText(sort)) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, property);
    }
}
