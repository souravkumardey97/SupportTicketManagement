package com.supportticket.api;

import com.supportticket.api.dto.UserSummaryResponse;
import com.supportticket.domain.Role;
import com.supportticket.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserSummaryResponse> listUsers(@RequestParam(required = false) Role role) {
        return userService.listUsers(role);
    }
}
