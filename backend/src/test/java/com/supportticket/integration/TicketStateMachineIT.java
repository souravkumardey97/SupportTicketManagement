package com.supportticket.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TicketStateMachineIT extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullHappyPathAndCancelledFlow() throws Exception {
        String adminToken = loginAsAdmin();

        Long ticketId = createTicket(adminToken);

        transition(adminToken, ticketId, "IN_PROGRESS");
        transition(adminToken, ticketId, "RESOLVED");
        transition(adminToken, ticketId, "CLOSED");

        Long cancelledId = createTicket(adminToken);
        transition(adminToken, cancelledId, "CANCELLED");
    }

    @Test
    void closedToOpenIsRejected() throws Exception {
        String adminToken = loginAsAdmin();
        Long ticketId = createTicket(adminToken);
        transition(adminToken, ticketId, "IN_PROGRESS");
        transition(adminToken, ticketId, "RESOLVED");
        transition(adminToken, ticketId, "CLOSED");

        mockMvc.perform(patch("/api/v1/tickets/" + ticketId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"OPEN\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("INVALID_STATE_TRANSITION"));
    }

    private String loginAsAdmin() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test-admin\",\"password\":\"test-admin-password\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private Long createTicket(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Issue\",\"description\":\"Details\",\"assigneeId\":2}"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private void transition(String token, Long ticketId, String status) throws Exception {
        mockMvc.perform(patch("/api/v1/tickets/" + ticketId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"" + status + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(status));
    }
}
