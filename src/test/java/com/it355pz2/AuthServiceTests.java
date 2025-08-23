package com.it355pz2;


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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUser() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("milica");
        dto.setPassword("1234");
        dto.setFullName("Milica");
        dto.setBio("bio");
        dto.setEmail("email");
        dto.setPhone("123");
        dto.setCity("Niš");
        dto.setUserType(UserType.freelancer); // Added userType

        when(passwordEncoder.encode("1234")).thenReturn("hashed1234");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        authService.register(dto);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("milica");
        assertThat(savedUser.getPassword()).isEqualTo("hashed1234");
        assertThat(savedUser.getUserType()).isEqualTo(UserType.freelancer);
    }

    @Test
    void shouldLoginUser() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("milica");
        loginDto.setPassword("1234");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        String token = authService.login(loginDto);

        assertThat(token).isEqualTo("jwt-token");
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        String header = "Bearer valid-token";
        when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);

        boolean result = authService.validate(header);

        assertThat(result).isTrue();
    }

    @Test
    void shouldFailTokenValidation() {
        String header = "Bearer invalid-token";
        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        boolean result = authService.validate(header);

        assertThat(result).isFalse();
    }
}