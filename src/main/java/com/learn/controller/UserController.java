package com.learn.controller;

import com.learn.model.dto.SpringSecurityUserDetailsDto;
import com.learn.model.dto.UserDto;
import com.learn.model.entity.User;
import com.learn.model.response.AppResponse;
import com.learn.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profiles")
    public ResponseEntity<?> createSimpleEntity(HttpServletRequest request) {
        SpringSecurityUserDetailsDto userContext = (SpringSecurityUserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<UserDto> optUser = userService.findByEmail(userContext.getEmail());
        AppResponse appResponse = AppResponse.buildResponse("", request.getRequestURI(), "Get profile successfully", HttpStatus.OK.value(), null);
        optUser.ifPresent(appResponse::setData);
        return new ResponseEntity<>(appResponse, HttpStatus.OK);
    }
}
