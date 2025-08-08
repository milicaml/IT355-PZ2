package com.it355pz2.services;

import com.it355pz2.dto.UserResponse;
import com.it355pz2.dto.UserUpdate;

public interface UserService {
    UserResponse getUser(Long id);

    UserResponse updateUser(Long id,UserUpdate updatedUser);

    boolean deleteUser(Long id);
}