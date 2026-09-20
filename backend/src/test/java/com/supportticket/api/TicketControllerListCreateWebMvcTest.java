package com.supportticket.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supportticket.api.dto.CreateTicketRequest;
import com.supportticket.api.dto.TicketPageResponse;
import com.supportticket.api.dto.TicketResponse;
import com.supportticket.api.dto.UserSummaryResponse;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TicketController.class)
@WebMvcSecurityTestImports
class TicketControllerListCreateWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void listTicketsSupportsPaginationAndFilters() throws Exception {
        TicketResponse ticket = sampleTicket();
        when(ticketService.listTickets(eq("login"), eq(TicketStatus.OPEN), eq(0), eq(20), eq("createdAt,desc")))
                .thenReturn(new TicketPageResponse(List.of(ticket), 0, 20, 1, 1));

        mockMvc.perform(get("/api/v1/tickets")
                        .param("keyword", "login")
                        .param("status", "OPEN")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Unable to login"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTicketReturns201() throws Exception {
        when(ticketService.createTicket(any(CreateTicketRequest.class))).thenReturn(sampleTicket());

        CreateTicketRequest request = new CreateTicketRequest("Unable to login", "Details", TicketPriority.P1, 2L);
        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTicketValidationFailsWhenTitleMissing() throws Exception {
        String body = "{\"title\":\"\",\"description\":\"x\",\"assigneeId\":2}";
        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTicketValidationFailsWhenAssigneeMissing() throws Exception {
        String body = "{\"title\":\"Valid\",\"description\":\"x\"}";
        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.assigneeId").exists());
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
