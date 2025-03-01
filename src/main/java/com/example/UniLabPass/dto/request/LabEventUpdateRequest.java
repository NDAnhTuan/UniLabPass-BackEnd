package com.example.UniLabPass.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabEventUpdateRequest {
    @Schema(example = "aaa-aaa-aaa")
    String eventId;

    @Schema(example = "abc-123-abc")
    String labId;

    @Schema(example = "Unilab's Event")
    String name;

    @Schema(example = "2025-03-01T10:00:00")
    LocalDateTime startTime;

    @Schema(example = "2025-03-01T11:00:00")
    LocalDateTime endTime;
}
