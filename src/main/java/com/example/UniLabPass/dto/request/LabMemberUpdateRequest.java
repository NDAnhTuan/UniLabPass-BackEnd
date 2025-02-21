package com.example.UniLabPass.dto.request;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabMemberUpdateRequest {
    LabMemberKey labMemberKey;
    @Schema(example = "ACTIVE")
    MemberStatus memberStatus;
}
