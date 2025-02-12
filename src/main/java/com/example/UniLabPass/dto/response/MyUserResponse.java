package com.example.UniLabPass.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyUserResponse {
    @Schema(example = "abc-123-abc")
    String id;
    @Schema(example = "nguyenducanhtuan0602@gmail.com")
    String email;
    @Schema(example = "Tuan")
    String firstName;
    @Schema(example = "Nguyen Duc")
    String lastName;
    @Schema(example = "2003-11-30")
    LocalDate dob;

    Set<RoleResponse> roles;
}
