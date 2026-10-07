package com.shopease.user.service;

import com.shopease.user.exception.ResourceNotFoundException;
import com.shopease.user.model.Role;
import com.shopease.user.model.User;
import com.shopease.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public List<User> getAllUsers(String role, Boolean active, String search) {
        List<User> users = userRepository.findAll();

        return users.stream()
                .filter(u -> {
                    if (role != null && !role.isBlank()) {
                        return u.getRole().name().equalsIgnoreCase(role);
                    }
                    return true;
                })
                .filter(u -> {
                    if (active != null) {
                        return u.isActive() == active;
                    }
                    return true;
                })
                .filter(u -> {
                    if (search != null && !search.isBlank()) {
                        String s = search.toLowerCase();
                        return (u.getName() != null && u.getName().toLowerCase().contains(s)) ||
                               (u.getEmail() != null && u.getEmail().toLowerCase().contains(s));
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public User toggleUserStatus(String id) {
        User user = getUserById(id);
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public Map<String, Long> getUserCounts() {
        Map<String, Long> map = new HashMap<>();
        map.put("totalCustomers", userRepository.countByRole(Role.CUSTOMER));
        map.put("totalSellers", userRepository.countByRole(Role.SELLER));
        map.put("totalAdmins", userRepository.countByRole(Role.ADMIN));
        map.put("totalUsers", userRepository.count());
        return map;
    }
}
