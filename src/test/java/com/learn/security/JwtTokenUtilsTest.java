package com.learn.security;

import com.learn.model.entity.User;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 30 test cases
@ExtendWith(MockitoExtension.class)
public class JwtTokenUtilsTest {

    @InjectMocks
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private User user;

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        SECRET_KEY = "1A2B3C4D5E6F7A8B9C0D1E2F3A4B5C6D7E8F9A0B1C2D3E4F5A6B7C8D9E0F1A2B3C4D5E6F7A8B9C0D1E2F3A4B5C6D";

        // Set the SECRET_KEY field in JwtTokenUtils using reflection
        Field secretKeyField = JwtTokenUtils.class.getDeclaredField("SECRET_KEY");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtTokenUtils, SECRET_KEY);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/jwt_test_data.csv", numLinesToSkip = 1)
    public void testGenerateAccessToken(String email, String expectedSubject, String tokenStatus) {
        when(user.getEmail()).thenReturn(email);

        String token = jwtTokenUtils.generateAccessToken(user);
        assertNotNull(token);

        String subject = jwtTokenUtils.getSubject(token);
        System.out.println(subject);
        assertEquals(String.valueOf(expectedSubject), String.valueOf(subject));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/jwt_test_data.csv", numLinesToSkip = 1)
    public void testValidateAccessToken(String email, String expectedSubject, String tokenStatus) {
        String token;
        if ("expired".equals(tokenStatus)) {
            token = Jwts.builder()
                    .setSubject("expired@example.com")
                    .setIssuer("expired@example.com")
                    .setIssuedAt(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000 - 1000)) // expired 24 hours ago
                    .setExpiration(new Date(System.currentTimeMillis() - 1000))
                    .signWith(jwtTokenUtils.getSignInKey(), SignatureAlgorithm.HS512)
                    .compact();
            assertFalse(jwtTokenUtils.validateAccessToken(token));
        } else if ("invalid".equals(tokenStatus)) {
            token = "invalidToken";
            assertFalse(jwtTokenUtils.validateAccessToken(token));
        } else {
            when(user.getEmail()).thenReturn(email);
            token = jwtTokenUtils.generateAccessToken(user);
            assertTrue(jwtTokenUtils.validateAccessToken(token));
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/jwt_test_data.csv", numLinesToSkip = 1)
    public void testGetSubject(String email, String expectedSubject, String tokenStatus) {
        if ("invalid".equals(tokenStatus) || "expired".equals(tokenStatus)) {
            // No need to test getSubject for invalid or expired tokens
            return;
        }
        when(user.getEmail()).thenReturn(email);

        String token = jwtTokenUtils.generateAccessToken(user);
        String subject = jwtTokenUtils.getSubject(token);

        assertEquals(expectedSubject, subject);
    }
}
