package com.devtrack.service.user;

import com.devtrack.dto.user.LoginRequest;
import com.devtrack.dto.user.RegisterRequest;
import com.devtrack.entity.user.User;

public interface UserService {

    User registerUser(RegisterRequest registerRequest);

    User loginUser(LoginRequest loginRequest);
}