package com.example.UniLabPass.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabDeleteRequest {
    @Schema(example = "abc-123-abc")
    String labId;

    @Schema(example = "101795")
    String adminId;
}
