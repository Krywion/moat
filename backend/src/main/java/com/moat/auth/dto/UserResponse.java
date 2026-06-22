package com.moat.auth.dto;

import com.moat.user.User;

import java.util.UUID;

public record UserResponse(UUID id, String email, String role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole().name());
    }
}
