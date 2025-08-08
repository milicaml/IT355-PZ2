package com.it355pz2.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterDto {
    private String username;
    private String password;
    private String fullName;
    private String bio;
    private String email;
    private String phone;
    private String city;
}
