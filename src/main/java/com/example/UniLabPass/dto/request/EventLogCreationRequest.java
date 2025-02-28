package com.example.UniLabPass.dto.request;

import com.example.UniLabPass.enums.RecordType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventLogCreationRequest {
    @Schema(example = "abc-123-abc")
    String eventId;

    @Schema(example = "2112551")
    String guestId;

    @Schema(example = "CHECKIN/CHECKOUT")
    RecordType recordType;

    @Schema(example = "https://firebasestorage.googleapis.com/v0/b/your-project-id.appspot.com/o/images%2Fprofile.jpg?alt=media")
    String photoURL;
}
