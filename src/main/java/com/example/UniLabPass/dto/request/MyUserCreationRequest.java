package com.example.UniLabPass.dto.request;

import com.example.UniLabPass.validator.DobConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyUserCreationRequest {
    @Schema(example = "2115177")
    String id;

    @Schema(example = "nguyenducanhtuan0602@gmail.com", required = true)
    @Size(min = 4, message = "USERNAME_INVALID")
    String email;

    @Schema(example = "12345678", required = true)
    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;

    @Schema(example = "Tuan")
    String firstName;

    @Schema(example = "Nguyen Duc Anh")
    String lastName;

    @Schema(description = "Date of birth", example = "2025-12-31")
    @DobConstraint(min = 16, message = "INVALID_DOB")
    LocalDate dob;

    @Schema(example = "MALE/FEMALE/NONE")
    String gender;
}
