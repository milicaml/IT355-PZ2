package com.it355pz2.dto;

import com.it355pz2.entity.enums.UserType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserUpdate {

    private String username;
    private String password;
    private String fullName;
    private String bio;
    private String email;
    private String phone;
    private String city;
    private UserType userType;
}
