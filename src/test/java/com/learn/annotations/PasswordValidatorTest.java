package com.learn.annotations;

import com.learn.annotations.validator.PasswordValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PasswordValidatorTest {
    private PasswordValidator passwordValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    public void setUp() {
        passwordValidator = new PasswordValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    public void testValidPassword() {
        // Given
        String validPassword = "Password123";

        // When
        boolean isValid = passwordValidator.isValid(validPassword, context);

        // Then
        assertTrue(isValid, "Password should be valid");
    }

    @Test
    public void testPasswordTooShort() {
        // Given
        String shortPassword = "Pwd1";

        // When
        boolean isValid = passwordValidator.isValid(shortPassword, context);

        // Then
        assertFalse(isValid, "Password is too short and should be invalid");
    }

    @Test
    public void testPasswordWithoutNumber() {
        // Given
        String passwordWithoutNumber = "Password";

        // When
        boolean isValid = passwordValidator.isValid(passwordWithoutNumber, context);

        // Then
        assertFalse(isValid, "Password without a number should be invalid");
    }

    @Test
    public void testPasswordWithoutLetter() {
        // Given
        String passwordWithoutLetter = "12345678";

        // When
        boolean isValid = passwordValidator.isValid(passwordWithoutLetter, context);

        // Then
        assertFalse(isValid, "Password without a letter should be invalid");
    }

    @Test
    public void testNullPassword() {
        // Given
        String nullPassword = null;

        // When
        boolean isValid = passwordValidator.isValid(nullPassword, context);

        // Then
        assertFalse(isValid, "Null password should be invalid");
    }

    @Test
    public void testEmptyPassword() {
        // Given
        String emptyPassword = "";

        // When
        boolean isValid = passwordValidator.isValid(emptyPassword, context);

        // Then
        assertFalse(isValid, "Empty password should be invalid");
    }

    @Test
    public void testPasswordWithSpecialCharacters() {
        // Given
        String passwordWithSpecialChars = "Password@123";

        // When
        boolean isValid = passwordValidator.isValid(passwordWithSpecialChars, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    public void testPasswordWithSpaces() {
        // Given
        String passwordWithSpaces = "Pass word1";

        // When
        boolean isValid = passwordValidator.isValid(passwordWithSpaces, context);

        // Then
        assertFalse(isValid, "Password with spaces should be invalid according to the regex");
    }

    @Test
    public void testPasswordWithOnlySpecialCharacters() {
        // Given
        String passwordWithOnlySpecialChars = "!@#$%^&*";

        // When
        boolean isValid = passwordValidator.isValid(passwordWithOnlySpecialChars, context);

        // Then
        assertFalse(isValid, "Password with only special characters should be invalid");
    }

    @Test
    public void testPasswordWithOnlySpaces() {
        // Given
        String passwordWithOnlySpaces = "        ";

        // When
        boolean isValid = passwordValidator.isValid(passwordWithOnlySpaces, context);

        // Then
        assertFalse(isValid, "Password with only spaces should be invalid");
    }
}
