package com.pm.authservice.utils;

import com.pm.authservice.dto.response.UserResponse;
import com.pm.authservice.model.Role;
import com.pm.authservice.model.User;

import java.util.stream.Collectors;


public class Mapper {

    public static UserResponse mapToUserResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .roles(user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

}
