package com.supportticket.persistence;

import com.supportticket.domain.Role;
import com.supportticket.domain.TicketPriority;
import com.supportticket.domain.TicketStatus;
import com.supportticket.persistence.entity.TicketEntity;
import com.supportticket.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class TicketRepositoryTest extends PersistenceTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private UserEntity assignee;

    @BeforeEach
    void seedUsers() {
        assignee = new UserEntity();
        assignee.setUsername("agent1");
        assignee.setPasswordHash("hash");
        assignee.setRole(Role.USER);
        assignee = userRepository.save(assignee);
    }

    @Test
    void searchIsCaseInsensitiveAndFiltersByStatus() {
        saveTicket("Login Failure", "Cannot sign in", TicketStatus.OPEN);
        saveTicket("Billing issue", "Invoice", TicketStatus.OPEN);
        saveTicket("Login timeout", "Session", TicketStatus.CLOSED);

        Page<TicketEntity> results = ticketRepository.searchByStatusAndKeyword(
                TicketStatus.OPEN,
                "login",
                PageRequest.of(0, 20));

        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getContent().getFirst().getTitle()).isEqualTo("Login Failure");
    }

    @Test
    void findByStatusWhenKeywordNotUsed() {
        saveTicket("A", "one", TicketStatus.OPEN);
        saveTicket("B", "two", TicketStatus.CLOSED);

        Page<TicketEntity> openTickets = ticketRepository.findByStatus(TicketStatus.OPEN, PageRequest.of(0, 20));
        assertThat(openTickets.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findAllReturnsEveryTicketWhenNoFilters() {
        saveTicket("A", "one", TicketStatus.OPEN);
        saveTicket("B", "two", TicketStatus.CLOSED);

        Page<TicketEntity> all = ticketRepository.findAll(PageRequest.of(0, 20));
        assertThat(all.getTotalElements()).isEqualTo(2);
    }

    @Test
    void searchByKeywordMatchesAssigneeUsernameCaseInsensitively() {
        saveTicket("Generic", "desc", TicketStatus.OPEN, TicketPriority.P1);
        UserEntity other = new UserEntity();
        other.setUsername("supportBob");
        other.setPasswordHash("hash");
        other.setRole(Role.USER);
        other = userRepository.save(other);
        TicketEntity bobTicket = new TicketEntity();
        bobTicket.setTitle("Other");
        bobTicket.setDescription("x");
        bobTicket.setPriority(TicketPriority.P2);
        bobTicket.setStatus(TicketStatus.OPEN);
        bobTicket.setAssignee(other);
        ticketRepository.save(bobTicket);

        Page<TicketEntity> results = ticketRepository.searchByKeyword("BOB", PageRequest.of(0, 20));
        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getContent().getFirst().getAssignee().getUsername()).isEqualTo("supportBob");
    }

    @Test
    void searchByKeywordMatchesPriorityCaseInsensitively() {
        saveTicket("Low urgency", "desc", TicketStatus.OPEN, TicketPriority.P2);
        saveTicket("High urgency", "desc", TicketStatus.OPEN, TicketPriority.P0);

        Page<TicketEntity> results = ticketRepository.searchByKeyword("p0", PageRequest.of(0, 20));
        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getContent().getFirst().getPriority()).isEqualTo(TicketPriority.P0);
    }

    @Test
    void findAllReturnsEmptyPageWhenNoTicketsExist() {
        Page<TicketEntity> all = ticketRepository.findAll(PageRequest.of(0, 20));
        assertThat(all.getTotalElements()).isZero();
        assertThat(all.getContent()).isEmpty();
    }

    private void saveTicket(String title, String description, TicketStatus status) {
        saveTicket(title, description, status, TicketPriority.P1);
    }

    private void saveTicket(String title, String description, TicketStatus status, TicketPriority priority) {
        TicketEntity ticket = new TicketEntity();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setStatus(status);
        ticket.setAssignee(assignee);
        ticketRepository.save(ticket);
    }
}
