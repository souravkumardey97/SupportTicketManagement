package com.supportticket.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supportticket.api.dto.TicketDetailResponse;
import com.supportticket.api.dto.TicketResponse;
import com.supportticket.api.dto.UpdateTicketRequest;
import com.supportticket.api.dto.UserSummaryResponse;
import com.supportticket.domain.Role;
import com.supportticket.domain.TicketPriority;
import com.supportticket.domain.TicketStatus;
import com.supportticket.exception.ResourceNotFoundException;
import com.supportticket.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TicketController.class)
@WebMvcSecurityTestImports
class TicketControllerDetailUpdateWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTicketReturnsDetail() throws Exception {
        TicketResponse ticket = sampleTicket();
        when(ticketService.getTicket(1L)).thenReturn(new TicketDetailResponse(ticket, List.of()));

        mockMvc.perform(get("/api/v1/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticket.id").value(1))
                .andExpect(jsonPath("$.ticket.title").value("Unable to login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTicketReturns404WhenMissing() throws Exception {
        when(ticketService.getTicket(99L)).thenThrow(new ResourceNotFoundException("Ticket not found"));

        mockMvc.perform(get("/api/v1/tickets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchTicketUpdatesFields() throws Exception {
        TicketResponse updated = new TicketResponse(
                1L, "Updated title", "Details", TicketPriority.P0, TicketStatus.OPEN,
                new UserSummaryResponse(2L, "agent1", Role.USER),
                Instant.parse("2026-09-19T10:00:00Z"), Instant.parse("2026-09-19T11:00:00Z"));
        when(ticketService.updateTicket(eq(1L), any(UpdateTicketRequest.class))).thenReturn(updated);

        UpdateTicketRequest request = new UpdateTicketRequest(
                "Updated title", "Updated description", TicketPriority.P0, 2L);
        mockMvc.perform(patch("/api/v1/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.priority").value("P0"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchTicketRejectsNullOrEmptyTitlePriorityAndAssignee() throws Exception {
        mockMvc.perform(patch("/api/v1/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "description": "Still here",
                                  "priority": null,
                                  "assigneeId": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.title").value("Title must not be null or empty"))
                .andExpect(jsonPath("$.fieldErrors.priority").value("Priority must not be null"))
                .andExpect(jsonPath("$.fieldErrors.assigneeId").value("Assignee must not be null"));
    }

    private static TicketResponse sampleTicket() {
        return new TicketResponse(
                1L,
                "Unable to login",
                "Details",
                TicketPriority.P1,
                TicketStatus.OPEN,
                new UserSummaryResponse(2L, "agent1", Role.USER),
                Instant.parse("2026-09-19T10:00:00Z"),
                Instant.parse("2026-09-19T10:00:00Z")
        );
    }
}
