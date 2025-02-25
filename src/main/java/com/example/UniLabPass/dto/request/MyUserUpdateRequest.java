package com.example.UniLabPass.dto.request;

import com.example.UniLabPass.validator.DobConstraint;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyUserUpdateRequest {
    @Schema(example = "2115177", required = true)
    String id;
    @Size(min = 6, message = "INVALID_PASSWORD")
    @Schema(example = "12345678")
    String password;

    @Schema(example = "Tuan")
    String firstName;
    @Schema(example = "Nguyen Duc")
    String lastName;
    @Schema(example = "NONE")
    String gender;


    @DobConstraint(min = 16, message = "INVALID_DOB")
    @Schema(example = "2003-11-30")
    LocalDate dob;
    @Schema(type = "array", example = "[\"MANAGER\", \"USER\"]")

    List<String> roles;
}
