package com.supportticket.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supportticket.api.dto.CreateUserRequest;
import com.supportticket.api.dto.UserSummaryResponse;
import com.supportticket.domain.Role;
import com.supportticket.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@WebMvcSecurityTestImports
class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanListUsersByRole() throws Exception {
        when(userService.listUsers(Role.USER)).thenReturn(List.of(new UserSummaryResponse(2L, "agent1", Role.USER)));

        mockMvc.perform(get("/api/v1/users").param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("agent1"))
                .andExpect(jsonPath("$[0].role").value("USER"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userRoleIsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users").param("role", "USER"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateUser() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenReturn(new UserSummaryResponse(5L, "newagent", Role.USER));

        CreateUserRequest request = new CreateUserRequest("newagent", "password12", Role.USER);
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.username").value("newagent"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserRejectsInvalidBody() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"","password":"","role":null}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.username").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists())
                .andExpect(jsonPath("$.fieldErrors.role").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userRoleCannotCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("newagent", "password12", Role.USER);
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }
}
