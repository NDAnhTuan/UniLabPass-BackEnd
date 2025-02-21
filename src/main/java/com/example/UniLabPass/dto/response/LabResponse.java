package com.example.UniLabPass.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabResponse {
    @Schema(example = "123-abc-123")
    String id;

    @Schema(example = "UniLab HCMUT")
    String name;

    @Schema(example = "268 Ly Thuong Kiet Street Ward 14, District 10 Ho Chi Minh City, Vietnam")
    String location;

    @Schema(example = "0")
    int capacity;
}