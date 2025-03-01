package com.example.UniLabPass.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DailyReportResponse {
    String dayOfWeek;
    int checkInCount;
    int checkOutCount;
}
