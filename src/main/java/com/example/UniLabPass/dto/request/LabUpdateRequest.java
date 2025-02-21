package com.example.UniLabPass.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabUpdateRequest {
    @Schema(example = "Another Lab Name", required = true)
    String name;

    @Schema(example = "Ly Thuong Kiet Street Ward 14, District 10 Ho Chi Minh City, Vietnam")
    String location;
}
