package com.example.UniLabPass.dto.response;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.entity.Lab;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.entity.Role;
import com.example.UniLabPass.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabMemberResponse {
    LabMemberKey labMemberId;
    MyUser myUser;
    Lab lab;
    Role role;
    MemberStatus memberStatus;
}
