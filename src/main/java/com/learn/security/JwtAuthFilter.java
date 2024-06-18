package com.learn.security;


import com.learn.model.dto.SpringSecurityUserDetailsDto;
import com.learn.model.dto.UserDto;
import com.learn.model.entity.AccountPaymentEntity;
import com.learn.service.impl.PaymentService;
import com.learn.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    public static AccountPaymentEntity accountPayment;

    public JwtAuthFilter(JwtTokenUtils jwtTokenUtils,
                         UserDetailsService userDetailsService, UserService userService) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
            return false;
        }

        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException {
        // Validate format token
        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        String email = jwtTokenUtils.getSubject(token);

        // Validate token
        if (!jwtTokenUtils.validateAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!ObjectUtils.isEmpty(email) && ObjectUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication())) {
            UserDetails userDetails = getUserDetails(token);

            UsernamePasswordAuthenticationToken
                    authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // setup account payment
            initAccount();
        }

        filterChain.doFilter(request, response);
    }

    private UserDetails getUserDetails(String token) {
        String email = jwtTokenUtils.getSubject(token);
        if (ObjectUtils.isEmpty(email)) {
            return null;
        }

        return userDetailsService.loadUserByUsername(email);
    }

    private void initAccount() {
        SpringSecurityUserDetailsDto userContext = (SpringSecurityUserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<UserDto> optUser = userService.findByEmail(userContext.getEmail());
        if (optUser.isPresent()) {
            accountPayment = new AccountPaymentEntity(optUser.get());
            accountPayment.setBills(PaymentService.initBills());
        } else {
            accountPayment = new AccountPaymentEntity();
            accountPayment.setBills(PaymentService.initBills());
        }
    }
}
