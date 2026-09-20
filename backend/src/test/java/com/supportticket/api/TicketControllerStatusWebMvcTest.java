package com.supportticket.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supportticket.api.dto.TicketResponse;
import com.supportticket.api.dto.UpdateTicketStatusRequest;
import com.supportticket.api.dto.UserSummaryResponse;
import com.supportticket.domain.InvalidTicketStateTransitionException;
import com.supportticket.domain.Role;
import com.supportticket.domain.TicketPriority;
import com.supportticket.domain.TicketStatus;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TicketController.class)
@WebMvcSecurityTestImports
class TicketControllerStatusWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void validStatusTransitionReturns200() throws Exception {
        TicketResponse updated = new TicketResponse(
                1L, "Title", "Desc", TicketPriority.P1, TicketStatus.IN_PROGRESS,
                new UserSummaryResponse(2L, "agent1", Role.USER),
                Instant.parse("2026-09-19T10:00:00Z"), Instant.parse("2026-09-19T10:05:00Z"));
        when(ticketService.updateTicketStatus(1L, TicketStatus.IN_PROGRESS)).thenReturn(updated);

        UpdateTicketStatusRequest request = new UpdateTicketStatusRequest(TicketStatus.IN_PROGRESS);
        mockMvc.perform(patch("/api/v1/tickets/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void invalidStatusTransitionReturns409() throws Exception {
        when(ticketService.updateTicketStatus(1L, TicketStatus.OPEN))
                .thenThrow(new InvalidTicketStateTransitionException(TicketStatus.CLOSED, TicketStatus.OPEN));

        UpdateTicketStatusRequest request = new UpdateTicketStatusRequest(TicketStatus.OPEN);
        mockMvc.perform(patch("/api/v1/tickets/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("INVALID_STATE_TRANSITION"));
    }
}
