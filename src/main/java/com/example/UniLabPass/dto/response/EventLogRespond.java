package com.example.UniLabPass.dto.response;

import com.example.UniLabPass.enums.LogStatus;
import com.example.UniLabPass.enums.RecordType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventLogRespond {
    String id;
    String guestId;
    String guestName;
    RecordType recordType;
    LocalDateTime recordTime;
    LogStatus status;
    String photoURL;
}
