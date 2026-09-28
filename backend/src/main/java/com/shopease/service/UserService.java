package com.shopease.service;

import com.shopease.dto.UserDto;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.Role;
import com.shopease.model.User;
import com.shopease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .address(user.getAddress())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : "")
                .build();
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsersByRole(String role) {
        Role r = Role.valueOf(role.toUpperCase());
        return userRepository.findByRole(r).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> searchUsers(String query) {
        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto toggleUserStatus(String userId) {
        User user = getUserById(userId);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        return convertToDto(user);
    }

    public void deleteUser(String userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    public UserDto updateProfile(String email, String name, String phone, String address) {
        User user = getUserByEmail(email);
        if (name != null && !name.isBlank()) user.setName(name);
        if (phone != null && !phone.isBlank()) user.setPhone(phone);
        if (address != null) user.setAddress(address);
        userRepository.save(user);
        return convertToDto(user);
    }

    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }
}
