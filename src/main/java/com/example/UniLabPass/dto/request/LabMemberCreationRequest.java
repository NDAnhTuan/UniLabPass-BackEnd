package com.example.UniLabPass.dto.request;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.entity.Role;
import com.fasterxml.jackson.databind.introspect.MemberKey;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabMemberCreationRequest {
    LabMemberKey memberKey;

    Role role;
}
