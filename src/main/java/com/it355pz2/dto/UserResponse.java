package com.it355pz2.dto;

import com.it355pz2.entity.User;
import com.it355pz2.entity.enums.UserType;
import lombok.Data;
import static com.it355pz2.utility.DateUtility.formatDate;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String bio;
    private String email;
    private String phone;
    private String city;
    private UserType userType;
    private String createdAt;

    public UserResponse(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.bio = user.getBio();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.city = user.getCity();
        this.userType = user.getUserType();
        this.createdAt = formatDate(user.getCreatedAt());
    }
}
