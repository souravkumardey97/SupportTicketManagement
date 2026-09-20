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

        Page<TicketEntity> results = ticketRepository.search(
                "login",
                TicketStatus.OPEN,
                PageRequest.of(0, 20));

        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getContent().getFirst().getTitle()).isEqualTo("Login Failure");
    }

    @Test
    void emptyKeywordReturnsAllWhenStatusMatches() {
        saveTicket("A", "one", TicketStatus.OPEN);
        saveTicket("B", "two", TicketStatus.CLOSED);

        Page<TicketEntity> openTickets = ticketRepository.search(null, TicketStatus.OPEN, PageRequest.of(0, 20));
        assertThat(openTickets.getTotalElements()).isEqualTo(1);
    }

    private void saveTicket(String title, String description, TicketStatus status) {
        TicketEntity ticket = new TicketEntity();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(TicketPriority.P1);
        ticket.setStatus(status);
        ticket.setAssignee(assignee);
        ticketRepository.save(ticket);
    }
}
