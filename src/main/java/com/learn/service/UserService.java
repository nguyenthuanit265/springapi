package com.learn.service;

import com.learn.model.dto.SpringSecurityUserDetailsDto;
import com.learn.model.dto.UserDto;
import com.learn.model.entity.User;
import com.learn.model.request.SignUpRequest;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> findByEmail(String email);

    Optional<SpringSecurityUserDetailsDto> findByUsername(String username);

    void signUp(SignUpRequest request);
}
