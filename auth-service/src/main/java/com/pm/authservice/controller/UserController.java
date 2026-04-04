package com.pm.authservice.controller;

import com.pm.authservice.dto.request.AssignRolesRequest;
import com.pm.authservice.dto.response.AssignRolesResponse;
import com.pm.authservice.dto.response.UserResponse;
import com.pm.authservice.dto.response.UserRolesResponse;
import com.pm.authservice.dto.response.UserStatusResponse;
import com.pm.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<UserResponse>> getUsers(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUser(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserStatusResponse> activateUser(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.activateUser(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserStatusResponse> deActivateUser(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.deActivateUser(id));
    }

    @PostMapping("/{id}/roles") // This end-point can perform assign/unassign roles for a specific user
    public ResponseEntity<AssignRolesResponse> assignRoles(@PathVariable UUID id, @RequestBody AssignRolesRequest assignRolesRequest){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.assignRoles(id, assignRolesRequest));
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<UserRolesResponse> getUserRoles(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getRolesById(id));
    }


}
