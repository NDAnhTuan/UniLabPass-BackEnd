package com.example.UniLabPass.dto.response;

import com.example.UniLabPass.entity.Lab;
import com.example.UniLabPass.entity.Role;
import com.example.UniLabPass.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabMemberResponse {
    @Schema(implementation = MyUserResponse.class)
    MyUserResponse myUserResponse;
    @Schema(example = "MEMBER")
    Role role;
    @Schema(example = "ACTIVE")
    MemberStatus memberStatus;
    @Schema()
    Lab lab;
}
