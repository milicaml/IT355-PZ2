package com.SE321;

import com.it355pz2.dto.LoginDto;
import com.it355pz2.dto.RegisterDto;
import com.it355pz2.entity.Token;
import com.it355pz2.entity.User;
import com.it355pz2.entity.enums.UserType;
import com.it355pz2.repository.TokenRepository;
import com.it355pz2.repository.UserRepository;
import com.it355pz2.security.JwtTokenProvider;
import com.it355pz2.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this); // Nije potrebno sa @ExtendWith(MockitoExtension.class)
    }

    
    @Test
    void shouldRegisterUser_Success() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setFullName("Test User");
        dto.setBio("Test bio");
        dto.setPhone("123456789");
        dto.setCity("Beograd");
        dto.setUserType(UserType.freelancer);

        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        authService.register(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getPassword()).isEqualTo("hashedPassword");
        assertThat(savedUser.getUserType()).isEqualTo(UserType.freelancer);
    }

    @Test
    void shouldRegisterUser_UsernameAlreadyExists() {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("new@example.com");
        dto.setUsername("existinguser");
        dto.setPassword("password123");
        dto.setFullName("New User");
        dto.setBio("Test bio");
        dto.setPhone("123456789");
        dto.setCity("Novi Sad");
        dto.setUserType(UserType.freelancer);

        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        authService.register(dto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldLoginUser_Success() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        String token = authService.login(loginDto);

        assertThat(token).isEqualTo("jwt-token");
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void shouldValidateToken_Success() {
        String header = "Bearer valid-token";
        when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);

        boolean result = authService.validate(header);

        assertThat(result).isTrue();
    }

    @Test
    void shouldValidateToken_Failure() {
        String header = "Bearer invalid-token";
        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        boolean result = authService.validate(header);

        assertThat(result).isFalse();
    }

    @Test
    void shouldValidateToken_InvalidHeader() {
        String header = "InvalidHeader";

        boolean result = authService.validate(header);

        assertThat(result).isFalse();
    }
}