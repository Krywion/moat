package com.moat.auth;

import com.moat.api.model.UserResponse;
import com.moat.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name());
    }
}
