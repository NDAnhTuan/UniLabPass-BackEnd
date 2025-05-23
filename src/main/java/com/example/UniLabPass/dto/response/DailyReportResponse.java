package com.example.UniLabPass.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DailyReportResponse {
    @Schema(example = "MONDAY")
    String dayOfWeek;

    @Schema(example = "10")
    int checkInCount;

    @Schema(example = "10")
    int checkOutCount;
}
