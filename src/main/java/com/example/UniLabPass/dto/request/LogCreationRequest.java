package com.example.UniLabPass.dto.request;

import com.example.UniLabPass.enums.LogType;
import com.example.UniLabPass.enums.RecordType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogCreationRequest {
    @Schema(example = "abc-123-abc")
    String labId;

    @Schema(example = "2112551")
    String userId;

    @Schema(example = "CHECKIN/CHECKOUT")
    RecordType recordType;

    @Schema(example = "LEGAL/ILLEGAL", description = "Illegal/legal identification field for facial authentication process (if wrong 3 times is illegal)")
    LogType logType;

    @Schema(example = "https://firebasestorage.googleapis.com/v0/b/your-project-id.appspot.com/o/images%2Fprofile.jpg?alt=media")
    String photoURL;
}
