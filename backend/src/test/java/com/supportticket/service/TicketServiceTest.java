package com.supportticket.service;

import com.supportticket.api.dto.CreateTicketRequest;
import com.supportticket.domain.Role;
import com.supportticket.domain.TicketPriority;
import com.supportticket.domain.TicketStatus;
import com.supportticket.domain.TicketStatusTransitionService;
import com.supportticket.exception.BadRequestException;
import com.supportticket.persistence.CommentRepository;
import com.supportticket.persistence.TicketRepository;
import com.supportticket.persistence.UserRepository;
import com.supportticket.persistence.entity.TicketEntity;
import com.supportticket.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TicketStatusTransitionService transitionService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    void listTicketsWithNoFiltersUsesFindAllAndReturnsEmptyPage() {
        when(ticketRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        var response = ticketService.listTickets(null, null, 0, 20, "createdAt,desc");

        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isZero();
        verify(ticketRepository).findAll(any(Pageable.class));
    }

    @Test
    void createTicketDefaultsPriorityToP1() {
        UserEntity assignee = user(Role.USER, 2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(ticketRepository.save(any(TicketEntity.class))).thenAnswer(invocation -> {
            TicketEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ticketService.createTicket(new CreateTicketRequest("Title", "Description", null, 2L));

        ArgumentCaptor<TicketEntity> captor = ArgumentCaptor.forClass(TicketEntity.class);
        verify(ticketRepository).save(captor.capture());
        assertThat(captor.getValue().getPriority()).isEqualTo(TicketPriority.P1);
        assertThat(captor.getValue().getStatus()).isEqualTo(TicketStatus.OPEN);
    }

    @Test
    void createTicketRejectsAdminAssignee() {
        UserEntity admin = user(Role.ADMIN, 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> ticketService.createTicket(
                new CreateTicketRequest("Title", "Description", TicketPriority.P1, 1L)))
                .isInstanceOf(BadRequestException.class);
    }

    private static UserEntity user(Role role, Long id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(role == Role.ADMIN ? "admin" : "agent1");
        user.setPasswordHash("hash");
        user.setRole(role);
        return user;
    }
}
