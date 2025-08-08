package com.it355pz2.services.impl;

import com.it355pz2.dto.UserResponse;
import com.it355pz2.dto.UserUpdate;
import com.it355pz2.entity.User;
import com.it355pz2.repository.UserRepository;
import com.it355pz2.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null || user.isDeleted()) return null;
        return new UserResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdate updatedUser) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null || user.isDeleted()) return null;

        if (updatedUser.getUsername() != null) user.setUsername(updatedUser.getUsername());
        if (updatedUser.getPassword() != null) {
            String encryptedPw = passwordEncoder.encode(updatedUser.getPassword());
            user.setPassword(encryptedPw);
        }
        if (updatedUser.getFullName() != null) user.setFullName(updatedUser.getFullName());
        if (updatedUser.getBio() != null) user.setBio(updatedUser.getBio());
        if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
        if (updatedUser.getPhone() != null) user.setPhone(updatedUser.getPhone());
        if (updatedUser.getCity() != null) user.setCity(updatedUser.getCity());
        if (updatedUser.getUserType() != null) user.setUserType(updatedUser.getUserType());
        user.setUpdatedAt(new Date().toString());
        userRepository.save(user);

        return new UserResponse(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null || user.isDeleted()) return false;

        user.setDeleted(true);
        user.setUpdatedAt(new Date().toString());

        userRepository.save(user);
        return true;
    }
}