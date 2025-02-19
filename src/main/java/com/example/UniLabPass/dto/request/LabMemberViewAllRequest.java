package com.example.UniLabPass.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabMemberViewAllRequest {
    @Schema(example = "2112843")
    String labId;

    @Schema(example = "101060")
    String adminId;
}
