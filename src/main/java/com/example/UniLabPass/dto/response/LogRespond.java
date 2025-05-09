package com.example.UniLabPass.dto.response;

import com.example.UniLabPass.enums.LogStatus;
import com.example.UniLabPass.enums.LogType;
import com.example.UniLabPass.enums.RecordType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogRespond {
    String id;
    String userId;
    String userFirstName;
    String userLastName;
    RecordType recordType;
    LocalDateTime recordTime;
    LogStatus status;
    LogType logType;
    String photoURL; // Use this to get info of photo used
}
