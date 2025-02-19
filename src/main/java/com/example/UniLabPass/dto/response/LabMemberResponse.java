package com.example.UniLabPass.dto.response;

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
    @Schema(example = "123asd123", required = true)
    String labId;
    @Schema(implementation = MyUserResponse.class)
    MyUserResponse myUserResponse;
    @Schema(example = "MEMBER")
    String role;
    @Schema(example = "ACTIVE")
    MemberStatus memberStatus;
}
