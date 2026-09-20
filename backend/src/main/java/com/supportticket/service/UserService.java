package com.supportticket.service;

import com.supportticket.api.dto.CreateUserRequest;
import com.supportticket.api.dto.UserSummaryResponse;
import com.supportticket.domain.Role;

import java.util.List;

public interface UserService {

    List<UserSummaryResponse> listUsers(Role role);

    UserSummaryResponse createUser(CreateUserRequest request);
}
