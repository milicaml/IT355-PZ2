package com.it355pz2.services;


import com.it355pz2.dto.LoginDto;
import com.it355pz2.dto.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);
    void register(RegisterDto registerDto);

    boolean validate(String authHeader);
}
