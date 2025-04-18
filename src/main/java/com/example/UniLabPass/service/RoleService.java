package com.example.UniLabPass.service;

import com.example.UniLabPass.dto.request.RoleRequest;
import com.example.UniLabPass.dto.response.RoleResponse;
import com.example.UniLabPass.mapper.RoleMapper;
import com.example.UniLabPass.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
//    PermissionRepository permissionRepository;
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);
//        var permissions = permissionRepository.findAllById(request.getPermissions());
        // ??? sao dùng HashSet
//        role.setPermissions(new HashSet<>(permissions));

        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<RoleResponse> getAll() {
        var roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).toList();
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(String role) {
        roleRepository.deleteById(role);
    }

}
