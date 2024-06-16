package com.learn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class HashPasswordTest {
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    public void testPasswordEncoderWorks() {
        // Directly testing PasswordEncoder
        String rawPassword = "123456";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
    }

    @Test
    public void testSamePasswordProducesDifferentHashes() {
        String password = "123456";
        String encodedPassword1 = passwordEncoder.encode(password);
        String encodedPassword2 = passwordEncoder.encode(password);
        assertNotSame(encodedPassword1, encodedPassword2);
    }

    @Test
    public void testVerifyPassword() {
        String password = "123456";
        String encodedPassword = passwordEncoder.encode(password);
        assertTrue(passwordEncoder.matches(password, encodedPassword));
    }

    @Test
    public void testVerifyPassword_Failure() {
        String password1 = "123456";
        String password2 = "1234567";
        String encodedPassword1 = passwordEncoder.encode(password1);
        assertFalse(passwordEncoder.matches(password2, encodedPassword1));
    }

    @Test
    public void testEncryptingNullOrEmptyPassword() {
        // Given
        String nullPassword = null;
        String emptyPassword = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            passwordEncoder.encode(nullPassword);
        });

        String encryptedEmptyPassword = passwordEncoder.encode(emptyPassword);
        assertNotNull(encryptedEmptyPassword);

        // Encrypted empty password should be different from raw empty string
        assertNotEquals(emptyPassword, encryptedEmptyPassword);
    }
}
