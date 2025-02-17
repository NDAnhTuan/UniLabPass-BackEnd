package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.RoleRequest;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.RoleResponse;
import com.example.UniLabPass.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;
    @Operation(summary = "Create Role", security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PostMapping
    CustomApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return CustomApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }
    @Operation(summary = "Get List Roles", security = {@SecurityRequirement(name = "Bearer Authentication")})
    @GetMapping
    CustomApiResponse<List<RoleResponse>> getAll() {
        return CustomApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }
    @Operation(summary = "Delete Role", security = {@SecurityRequirement(name = "Bearer Authentication")})
    @DeleteMapping("/{role}")
    CustomApiResponse<Void> delete(@PathVariable String role) {
        roleService.delete(role);
        return CustomApiResponse.<Void>builder().build();
    }
}
