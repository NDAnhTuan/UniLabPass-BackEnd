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
    @Schema(example = "Another Lab Name")
    String name;

    @Schema(example = "New Location on Thu Duc")
    String location;
}
