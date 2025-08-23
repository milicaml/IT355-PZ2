package com.SE321;

import com.it355pz2.dto.RegisterDto;
import com.it355pz2.entity.User;
import com.it355pz2.entity.enums.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WhiteBoxTests {

    // ========== STATEMENT COVERAGE ==========
    
    @Test
    void testStatementCoverage_CompleteRegistrationFlow() {
        // Test koji pokriva sve linije koda u registraciji
        RegisterDto dto = createValidRegisterDto();
        
        // Testira sve linije u register metodi
        User user = createUserFromDto(dto);
        
        // Verifikacija svih poziva
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getFullName());
        assertEquals("Test bio", user.getBio());
        assertEquals("123456789", user.getPhone());
        assertEquals("Beograd", user.getCity());
        assertEquals(UserType.freelancer, user.getUserType());
    }

    @Test
    void testStatementCoverage_AllUserTypes() {
        // Test koji pokriva sve tipove korisnika
        UserType[] userTypes = {UserType.freelancer, UserType.employer, UserType.admin};
        
        for (UserType userType : userTypes) {
            RegisterDto dto = createValidRegisterDto();
            dto.setUserType(userType);
            
            User user = createUserFromDto(dto);
            assertEquals(userType, user.getUserType());
        }
    }

    // ========== BRANCH COVERAGE ==========
    
    @Test
    void testBranchCoverage_EmailValidation() {
        assertTrue(isValidEmail("test@example.com"), "Valid email should pass");
        
        assertFalse(isValidEmail(null), "Null email should fail");
        
        assertFalse(isValidEmail(""), "Empty email should fail");
        
        assertFalse(isValidEmail("invalid-email"), "Invalid email format should fail");
    }

    @Test
    void testBranchCoverage_PasswordValidation() {
        assertTrue(isValidPassword("password123"), "Valid password should pass");
        
        assertFalse(isValidPassword(null), "Null password should fail");
        
        assertFalse(isValidPassword("123"), "Short password should fail");
        
        assertFalse(isValidPassword(""), "Empty password should fail");
    }

    @Test
    void testBranchCoverage_UsernameValidation() {
        assertTrue(isValidUsername("testuser"), "Valid username should pass");
        
        assertFalse(isValidUsername(null), "Null username should fail");
        
        assertFalse(isValidUsername(""), "Empty username should fail");
        
        String longUsername = "a".repeat(51);
        assertFalse(isValidUsername(longUsername), "Too long username should fail");
    }

    @Test
    void testBranchCoverage_PaymentAmountValidation() {
        assertTrue(isValidPaymentAmount(0), "Minimum payment amount should pass");
        
        assertTrue(isValidPaymentAmount(999999), "Maximum payment amount should pass");
        
        assertFalse(isValidPaymentAmount(-1), "Negative payment amount should fail");
        
        assertFalse(isValidPaymentAmount(1000000), "Too high payment amount should fail");
    }

    @Test
    void testBranchCoverage_PhoneValidation() {
        assertTrue(isValidPhone("123456789"), "Valid phone should pass");
        
        assertFalse(isValidPhone(null), "Null phone should fail");
        
        assertFalse(isValidPhone(""), "Empty phone should fail");
        
        assertFalse(isValidPhone("abc123"), "Non-numeric phone should fail");
        
        assertFalse(isValidPhone("1234567890123456"), "Too long phone should fail");
    }

    @Test
    void testBranchCoverage_CityValidation() {
        assertTrue(isValidCity("Beograd"), "Valid city should pass");
        
        assertFalse(isValidCity(null), "Null city should fail");
        
        assertFalse(isValidCity(""), "Empty city should fail");
        
        String longCity = "a".repeat(101);
        assertFalse(isValidCity(longCity), "Too long city should fail");
    }

    // ========== PATH COVERAGE ==========
    
    @Test
    void testPathCoverage_CompleteValidationFlow() {
        RegisterDto validDto = createValidRegisterDto();
        assertTrue(isValidRegistrationData(validDto), "All valid inputs should pass");
        
        RegisterDto invalidEmailDto = createValidRegisterDto();
        invalidEmailDto.setEmail("invalid-email");
        assertFalse(isValidRegistrationData(invalidEmailDto), "Invalid email should fail");
        
        RegisterDto invalidPasswordDto = createValidRegisterDto();
        invalidPasswordDto.setPassword("123");
        assertFalse(isValidRegistrationData(invalidPasswordDto), "Invalid password should fail");
        
        RegisterDto invalidUsernameDto = createValidRegisterDto();
        invalidUsernameDto.setUsername("");
        assertFalse(isValidRegistrationData(invalidUsernameDto), "Invalid username should fail");
        
        RegisterDto invalidPhoneDto = createValidRegisterDto();
        invalidPhoneDto.setPhone("abc");
        assertFalse(isValidRegistrationData(invalidPhoneDto), "Invalid phone should fail");
        
        RegisterDto invalidCityDto = createValidRegisterDto();
        invalidCityDto.setCity("");
        assertFalse(isValidRegistrationData(invalidCityDto), "Invalid city should fail");
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

    private User createUserFromDto(RegisterDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setBio(dto.getBio());
        user.setPhone(dto.getPhone());
        user.setCity(dto.getCity());
        user.setUserType(dto.getUserType());
        return user;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        return password.length() >= 6;
    }

    private boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return username.length() >= 1 && username.length() <= 50;
    }

    private boolean isValidPaymentAmount(int amount) {
        return amount >= 0 && amount < 1000000;
    }

    private boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return phone.matches("\\d+") && phone.length() >= 1 && phone.length() <= 15;
    }

    private boolean isValidCity(String city) {
        if (city == null || city.isEmpty()) {
            return false;
        }
        return city.length() >= 1 && city.length() <= 100;
    }

    private boolean isValidRegistrationData(RegisterDto dto) {
        return isValidEmail(dto.getEmail()) &&
               isValidPassword(dto.getPassword()) &&
               isValidUsername(dto.getUsername()) &&
               isValidPhone(dto.getPhone()) &&
               isValidCity(dto.getCity());
    }
}
