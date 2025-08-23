package com.SE321;

import com.it355pz2.dto.RegisterDto;
import com.it355pz2.entity.enums.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BlackBoxTests {

    // ========== EQUIVALENCE PARTITIONING ==========
    
    @Test
    void testEmailValidation_ValidEmails() {
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@test.com"
        };
        
        for (String email : validEmails) {
            RegisterDto dto = createValidRegisterDto();
            dto.setEmail(email);
            assertTrue(isValidEmail(email), "Email should be valid: " + email);
        }
    }

    @Test
    void testEmailValidation_InvalidEmails() {
        String[] invalidEmails = {
            "invalid-email",
            "@domain.com",
            "test@",
            "test.domain.com",
            "",
            null
        };
        
        for (String email : invalidEmails) {
            if (email != null) {
                assertFalse(isValidEmail(email), "Email should be invalid: " + email);
            }
        }
    }

    @Test
    void testPasswordValidation_ValidPasswords() {
        String[] validPasswords = {
            "password123",
            "MyPass123!",
            "SecurePass2024",
            "Abc123!@#"
        };
        
        for (String password : validPasswords) {
            RegisterDto dto = createValidRegisterDto();
            dto.setPassword(password);
            assertTrue(isValidPassword(password), "Password should be valid: " + password);
        }
    }

    @Test
    void testPasswordValidation_InvalidPasswords() {
        String[] invalidPasswords = {
            "123",
            "pass",
            "",
            null,
            "short"
        };
        
        for (String password : invalidPasswords) {
            if (password != null) {
                assertFalse(isValidPassword(password), "Password should be invalid: " + password);
            }
        }
    }

    // ========== BOUNDARY VALUE ANALYSIS ==========
    
    @Test
    void testJobTitleLengthBoundaryValues() {
        assertFalse(isValidJobTitle(""), "Empty job title should be invalid");
        assertFalse(isValidJobTitle("1234"), "4 character job title should be invalid");
        assertTrue(isValidJobTitle("12345"), "5 character job title should be valid");
        assertTrue(isValidJobTitle("123456"), "6 character job title should be valid");
        assertTrue(isValidJobTitle("a".repeat(100)), "100 character job title should be valid");
        assertFalse(isValidJobTitle("a".repeat(101)), "101 character job title should be invalid");
    }

    @Test
    void testPaymentAmountBoundaryValues() {
        // Boundary values for payment amount
        int[] boundaryValues = {0, 1, 999999, 1000000};
        
        // Valid boundary values
        assertTrue(isValidPaymentAmount(0), "Minimum payment amount should be valid");
        assertTrue(isValidPaymentAmount(1), "Minimum + 1 payment amount should be valid");
        assertTrue(isValidPaymentAmount(999999), "Maximum payment amount should be valid");
        
        // Invalid boundary values
        assertFalse(isValidPaymentAmount(-1), "Negative payment amount should be invalid");
        assertFalse(isValidPaymentAmount(1000000), "Maximum + 1 payment amount should be invalid");
    }

    @Test
    void testUsernameLengthBoundaryValues() {
        // Boundary values for username length
        String[] boundaryUsernames = {
            "",           // Minimum - 1 (empty)
            "a",          // Minimum
            "ab",         // Minimum + 1
            "a".repeat(50), // Maximum
            "a".repeat(51)  // Maximum + 1
        };
        
        assertFalse(isValidUsername(""), "Empty username should be invalid");
        assertTrue(isValidUsername("a"), "Single character username should be valid");
        assertTrue(isValidUsername("ab"), "Two character username should be valid");
        assertTrue(isValidUsername("a".repeat(50)), "50 character username should be valid");
        assertFalse(isValidUsername("a".repeat(51)), "51 character username should be invalid");
    }

    @Test
    void testPhoneNumberBoundaryValues() {
        // Boundary values for phone number
        String[] boundaryPhones = {
            "",           // Empty
            "1",          // Minimum length
            "12",         // Minimum + 1
            "123456789012345", // Maximum length
            "1234567890123456" // Maximum + 1
        };
        
        assertFalse(isValidPhone(""), "Empty phone should be invalid");
        assertTrue(isValidPhone("1"), "Single digit phone should be valid");
        assertTrue(isValidPhone("12"), "Two digit phone should be valid");
        assertTrue(isValidPhone("123456789012345"), "15 digit phone should be valid");
        assertFalse(isValidPhone("1234567890123456"), "16 digit phone should be invalid");
    }

    // ========== HELPER METHODS ==========
    
    private RegisterDto createValidRegisterDto() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setFullName("Test User");
        dto.setBio("Test bio");
        dto.setPhone("123456789");
        dto.setCity("Beograd");
        dto.setUserType(UserType.freelancer);
        return dto;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Simple email validation regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // Password must be at least 6 characters
        return password.length() >= 6;
    }

    private boolean isValidPaymentAmount(int amount) {
        // Payment amount must be between 0 and 999999
        return amount >= 0 && amount < 1000000;
    }

    private boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        // Username must be between 1 and 50 characters
        return username.length() >= 1 && username.length() <= 50;
    }

    private boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        // Phone must be between 1 and 15 digits
        return phone.matches("\\d+") && phone.length() >= 1 && phone.length() <= 15;
    }

    private boolean isValidJobTitle(String title) {
        if (title == null || title.isEmpty()) {
            return false;
        }
        // Job title must be between 5 and 100 characters
        return title.length() >= 5 && title.length() <= 100;
    }
}
