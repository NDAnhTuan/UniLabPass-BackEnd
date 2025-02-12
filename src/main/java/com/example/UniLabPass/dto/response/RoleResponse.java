package com.example.UniLabPass.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    @Schema(example = "ADMIN")
    String name;
    @Schema(example = "ADMIN Role")
    String description;
//    Set<PermissionResponse> permissions;

}