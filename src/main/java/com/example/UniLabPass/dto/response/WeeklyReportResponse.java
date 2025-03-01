package com.example.UniLabPass.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeeklyReportResponse {
    @Schema(example = "15")
    int totalUsers;

    @Schema(example = "112")
    int weeklyAccess;

    List<DailyReportResponse> weeklyLogReport;
}
