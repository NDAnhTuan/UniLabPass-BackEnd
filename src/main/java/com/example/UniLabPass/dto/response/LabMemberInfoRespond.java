package com.example.UniLabPass.dto.response;

import com.example.UniLabPass.entity.Lab;
import com.example.UniLabPass.entity.Role;
import com.example.UniLabPass.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabMemberInfoRespond {
    MyUserResponse myUserResponse;

    @Schema(example = "")
    MemberStatus status;
}
