package com.shopease.user.controller;

import com.shopease.user.dto.ApiResponse;
import com.shopease.user.model.User;
import com.shopease.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<List<User>>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search
    ) {
        List<User> users = userService.getAllUsers(role, active, search);
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @PutMapping("/admin/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<User>> toggleUserStatus(@PathVariable String id) {
        User user = userService.toggleUserStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("User status updated", user));
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted successfully", null));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    @GetMapping("/users/stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUserStats() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserCounts()));
    }
}
