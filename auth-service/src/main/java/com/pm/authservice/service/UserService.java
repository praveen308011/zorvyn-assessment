package com.pm.authservice.service;

import com.pm.authservice.dto.request.AssignRolesRequest;
import com.pm.authservice.dto.response.AssignRolesResponse;
import com.pm.authservice.dto.response.UserResponse;
import com.pm.authservice.dto.response.UserRolesResponse;
import com.pm.authservice.dto.response.UserStatusResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUser(UUID id);

    UserStatusResponse activateUser(UUID id);

    UserStatusResponse deActivateUser(UUID id);

    AssignRolesResponse assignRoles(UUID id, AssignRolesRequest assignRolesRequest);

    UserRolesResponse getRolesById(UUID id);
}
