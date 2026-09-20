package com.supportticket.service;

import com.supportticket.api.dto.LoginRequest;
import com.supportticket.api.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
