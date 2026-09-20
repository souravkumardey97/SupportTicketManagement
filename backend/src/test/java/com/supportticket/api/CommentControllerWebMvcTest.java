package com.supportticket.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supportticket.api.dto.CommentResponse;
import com.supportticket.api.dto.CreateCommentRequest;
import com.supportticket.api.dto.UserSummaryResponse;
import com.supportticket.domain.Role;
import com.supportticket.exception.ResourceNotFoundException;
import com.supportticket.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
@WebMvcSecurityTestImports
class CommentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void addCommentReturns201() throws Exception {
        CommentResponse response = new CommentResponse(
                10L,
                "Investigating",
                new UserSummaryResponse(1L, "admin", Role.ADMIN),
                Instant.parse("2026-09-19T12:00:00Z"));
        when(commentService.addComment(eq(1L), any(CreateCommentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tickets/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCommentRequest("Investigating"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.body").value("Investigating"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void blankCommentBodyReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/tickets/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.body").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void unknownTicketReturns404() throws Exception {
        when(commentService.addComment(eq(99L), any(CreateCommentRequest.class)))
                .thenThrow(new ResourceNotFoundException("Ticket not found"));

        mockMvc.perform(post("/api/v1/tickets/99/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCommentRequest("Note"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }
}
