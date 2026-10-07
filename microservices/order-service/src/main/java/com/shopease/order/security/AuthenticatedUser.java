package com.shopease.order.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser implements Principal {
    private String id;
    private String email;
    private String role;

    @Override
    public String getName() {
        return email;
    }
}
