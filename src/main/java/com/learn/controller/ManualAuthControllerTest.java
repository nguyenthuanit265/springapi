package com.learn.controller;

import com.learn.model.dto.CustomUserDetailsService;
import com.learn.model.entity.User;
import com.learn.model.request.AuthRequest;
import com.learn.model.response.AppResponse;
import com.learn.security.JwtTokenUtils;
import com.learn.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ManualAuthControllerTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtTokenUtils jwtUtils;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ManualAuthController manualAuthController;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginWithIncorrectCredentials() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("incorrect@example.com");
        authRequest.setPassword("wrongpassword");

        when(customUserDetailsService.findByEmail("incorrect@example.com")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = manualAuthController.login(authRequest, httpServletRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid username or password.", response.getBody());
    }
}
