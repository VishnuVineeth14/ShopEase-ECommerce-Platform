package com.shopease.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String address;
    private boolean enabled;
    private String createdAt;
}
