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
public class LabCreationRequest {
    @Schema(example = "New UniLab Name", required = true)
    String name;

    @Schema(example = "Ly Thuong Kiet Street Ward 14, District 10 Ho Chi Minh City, Vietnam")
    String location;
}
