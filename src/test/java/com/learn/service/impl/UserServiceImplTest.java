package com.learn.service.impl;


import com.learn.model.dto.SpringSecurityUserDetailsDto;
import com.learn.model.dto.UserDto;
import com.learn.model.entity.User;
import com.learn.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 6 test cases
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest servletRequest;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    public void setUp() {
//        when(servletRequest.getRequestURI()).thenReturn("/api/signup");
    }

    @Test
    public void testFindByEmail_withValidEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setName("Test User");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<UserDto> result = userServiceImpl.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        assertEquals("Test User", result.get().getName());
    }

    @Test
    public void testFindByEmail_withEmptyEmail() {
        Optional<UserDto> result = userServiceImpl.findByEmail("");

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByEmail_withNonExistentEmail() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<UserDto> result = userServiceImpl.findByEmail(email);

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByUsername_withValidUsername() {
        String username = "test@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(username);
        user.setName("Test User");

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        Optional<SpringSecurityUserDetailsDto> result = userServiceImpl.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getEmail());
        assertEquals("Test User", result.get().getName());
    }

    @Test
    public void testFindByUsername_withEmptyUsername() {
        Optional<SpringSecurityUserDetailsDto> result = userServiceImpl.findByUsername("");

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByUsername_withNonExistentUsername() {
        String username = "nonexistent@example.com";

        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        Optional<SpringSecurityUserDetailsDto> result = userServiceImpl.findByUsername(username);

        assertFalse(result.isPresent());
    }
}
