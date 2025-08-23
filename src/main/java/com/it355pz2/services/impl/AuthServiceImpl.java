package com.it355pz2.services.impl;

import com.it355pz2.dto.LoginDto;
import com.it355pz2.dto.RegisterDto;
import com.it355pz2.entity.Token;
import com.it355pz2.entity.User;
import com.it355pz2.entity.enums.UserType;
import com.it355pz2.repository.TokenRepository;
import com.it355pz2.repository.UserRepository;
import com.it355pz2.security.JwtTokenProvider;
import com.it355pz2.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.it355pz2.utility.TokenUtility.getTokenFromBearer;
import static com.it355pz2.utility.DateUtility.getCurrentDateTime;

@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private TokenRepository tokenRepository;

    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("AuthService - Login authorities: " + authentication.getAuthorities());

        String token = jwtTokenProvider.generateToken(authentication);

        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setCreatedAt(getCurrentDateTime());
        tokenEntity.setUpdatedAt(getCurrentDateTime());
//        tokenEntity.setUser((User) authentication.getPrincipal());
        tokenEntity.setUser(null); //! This need to be changed
        tokenEntity.setExpiresAt(new Date().toString()); //! This need to be changed
        tokenRepository.save(tokenEntity);

        return token;
    }

    @Override
    public void register(RegisterDto registerDto) {
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setFullName(registerDto.getFullName());
        user.setBio(registerDto.getBio());
        user.setEmail(registerDto.getEmail());
        user.setPhone(registerDto.getPhone());
        user.setCity(registerDto.getCity());
        user.setCreatedAt(getCurrentDateTime());
        user.setUpdatedAt(getCurrentDateTime());
        user.setUserType(registerDto.getUserType());
        userRepository.save(user);
    }

    @Override
    public boolean validate(String authHeader) {
        String token = getTokenFromBearer(authHeader);
        return jwtTokenProvider.validateToken(token);
    }
}