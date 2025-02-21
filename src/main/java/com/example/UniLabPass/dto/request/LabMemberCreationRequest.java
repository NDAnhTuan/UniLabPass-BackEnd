package com.example.UniLabPass.dto.request;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.entity.Role;
import com.example.UniLabPass.enums.MemberStatus;
import com.fasterxml.jackson.databind.introspect.MemberKey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabMemberCreationRequest {
    @Schema(example = "abc-123-abc")
    String labId;

    @Schema(example = "2112843")
    String userId;

    @Schema(example = "Ban")
    String firstName;

    @Schema(example = "Bui")
    String lastName;

    @Schema(example = "ban.bui114@hcmut.edu.vn")
    String email;

    @Schema(example = "0879090603")
    String phone;

    @Schema(example = "2003-04-11")
    LocalDate dob;

    @Schema(example = "MEMBER")
    String role;
}
