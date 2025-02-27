package com.example.UniLabPass.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabMemberCreationRequest   {
    @Schema(example = "123asd123", required = true)
    String labId;
    @Schema(example = "2115177", required = true)
    String userId;
    @Schema(example = "Tuan")
    String firstName;
    @Schema(example = "Nguyen Duc Anh")
    String lastName;
    @Schema(example = "ban.bui114@hcmut.edu.vn")
    String email;
    @Schema(example = "2003-04-11")
    LocalDate dob;
    @Schema(example = "MALE")
    String gender;
    @Schema(example = "MEMBER/MANAGER", required = true)
    String role;
}
