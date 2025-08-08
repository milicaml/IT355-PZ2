package com.it355pz2.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginDto {
    private String username;
    private String password;
}
