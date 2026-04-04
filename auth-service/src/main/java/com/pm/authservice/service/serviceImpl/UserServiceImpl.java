package com.pm.authservice.service.serviceImpl;

import com.pm.authservice.dto.request.AssignRolesRequest;
import com.pm.authservice.dto.response.AssignRolesResponse;
import com.pm.authservice.dto.response.UserResponse;
import com.pm.authservice.dto.response.UserRolesResponse;
import com.pm.authservice.dto.response.UserStatusResponse;
import com.pm.authservice.exception.*;
import com.pm.authservice.model.Permission;
import com.pm.authservice.model.Role;
import com.pm.authservice.model.User;
import com.pm.authservice.model.enums.Status;
import com.pm.authservice.repository.RoleRepository;
import com.pm.authservice.repository.UserRepository;
import com.pm.authservice.service.UserService;
import com.pm.authservice.utils.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.pm.authservice.utils.Mapper.mapToUserResponse;


@Service
@RequiredArgsConstructor
@Slf4j
 class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Override
    public List<UserResponse> getAllUsers() {

        log.info("Fetching all users");

        return userRepository.findAll()
                .stream()
                .map(Mapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUser(UUID id) {
        log.info("Fetching user with id: {}", id);

        User user = this.findUserById(id);
        return mapToUserResponse(user);
    }

    @Override
    public UserStatusResponse activateUser(UUID id) {
        log.info("Activating user with id: {}", id);

        User user = this.findUserById(id);

        if (user.getStatus() == Status.ACTIVE) {
            throw new UserAlreadyActiveException(
                    "User is already active"
            );
        }

        user.setStatus(Status.ACTIVE);
        userRepository.save(user);

        log.info("User {} activated successfully", id);

        return UserStatusResponse.builder()
                .id(user.getId())
                .message("User activated successfully")
                .status(user.getStatus().name())
                .build();
    }

    @Override
    public UserStatusResponse deActivateUser(UUID id) {
        log.info("Deactivating user with id: {}", id);

        User user = this.findUserById(id);

        if (user.getStatus() == Status.INACTIVE) {
            throw new UserAlreadyInactiveException(
                    "User is already active"
            );
        }

        user.setStatus(Status.INACTIVE);
        userRepository.save(user);

        log.info("User {} deactivated successfully", id);

        return UserStatusResponse.builder()
                .id(user.getId())
                .message("User activated successfully")
                .status(user.getStatus().name())
                .build();
    }

    @Override
    public AssignRolesResponse assignRoles(UUID id, AssignRolesRequest assignRolesRequest) {


        log.info("Assigning roles to user {}", id);

        User user = this.findUserById(id);

        Set<Role> roles = assignRolesRequest.getRoleIds()
                .stream()
                .map(roleId->roleRepository.findById(roleId)
                        .orElseThrow(()->new RoleNotFoundException(
                                "Role not found: "+ roleId
                        )))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        log.info("Roles assigned successfully to user {}", id);

        return AssignRolesResponse.builder()
                .userId(id)
                .message("Roles assigned successfully")
                .roles(savedUser.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public UserRolesResponse getRolesById(UUID id) {
        log.info("Fetching roles for user: {}", id);
        User user = this.findUserById(id);

        if (user.getRoles().isEmpty()) {
            throw new NoRolesAssignedException(
                    "User has no roles assigned"
            );
        }

        return UserRolesResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .roles(user.getRoles()
                        .stream()
                        .map(role->UserRolesResponse.RoleData.builder()
                                .id(role.getId())
                                .name(role.getName())
                                .permissions(role.getPermissions()
                                        .stream()
                                        .map(Permission::getName)
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet())
                )
                .build();
    }

    // helper method

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + id
                ));
    }

}
