package com.learn.service;

import com.learn.model.dto.SpringSecurityUserDetailsDto;
import com.learn.model.dto.UserDto;
import com.learn.model.request.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> findByEmail(String email);

    Optional<SpringSecurityUserDetailsDto> findByUsername(String username);

    ResponseEntity<?> signUp(SignUpRequest request, HttpServletRequest servletRequest);
}
