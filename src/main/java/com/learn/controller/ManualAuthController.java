package com.learn.controller;

import com.learn.model.dto.CustomUserDetailsService;
import com.learn.model.entity.User;
import com.learn.model.request.AuthRequest;
import com.learn.model.request.SignUpRequest;
import com.learn.model.response.AppResponse;
import com.learn.model.response.AuthResponse;
import com.learn.security.JwtTokenUtils;
import com.learn.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class ManualAuthController {

    private final Logger LOGGER = LoggerFactory.getLogger(ManualAuthController.class);
    private final AuthenticationManager authManager;
    private final JwtTokenUtils jwtUtils;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public ManualAuthController(AuthenticationManager authManager,
                                JwtTokenUtils jwtUtils,
                                ModelMapper modelMapper,
                                UserService userService,
                                CustomUserDetailsService customUserDetailsService,
                                PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest req, HttpServletRequest request) {
        return userService.signUp(req, request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request, HttpServletRequest req) {
        try {
//            // Spring Security use authenticate function -> call functions loadUserByUsername and get username and password -> using PasswordEncoder Bean authenticate user login
//            Authentication authentication = authManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.getEmail(), request.getPassword())
//            );
//
//            // Get user login info
//            SpringSecurityUserDetailsDto userByEmail = (SpringSecurityUserDetailsDto) authentication.getPrincipal();

            Optional<User> userByEmail = customUserDetailsService.findByEmail(request.getEmail());
            if (userByEmail.isEmpty() || !passwordEncoder.matches(request.getPassword(), userByEmail.get().getPassword())) {
                return ResponseEntity.badRequest().body("Invalid username or password.");
            }
            User user = modelMapper.map(userByEmail.get(), User.class);
            String accessToken = jwtUtils.generateAccessToken(user);

            // Response
            AuthResponse response = AuthResponse.builder().accessToken(accessToken).build();
            return ResponseEntity.ok(AppResponse.buildResponse("", req.getRequestURI(), HttpStatus.OK.name(), HttpStatus.OK.value(), response));

        } catch (BadCredentialsException ex) {
            ex.fillInStackTrace();
            return ResponseEntity.ok(AppResponse.buildResponse("", req.getRequestURI(), HttpStatus.UNAUTHORIZED.name(), HttpStatus.UNAUTHORIZED.value(), null));
        }
    }
}