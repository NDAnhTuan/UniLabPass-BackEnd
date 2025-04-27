package com.example.UniLabPass.dto.response;

import com.example.UniLabPass.enums.NotifyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    @Schema
    String id;
    @Schema
    String userId;
    @Schema
    String title;
    @Schema
    String body;
    @Schema
    NotifyType type;
    @Schema
    Date createdAt;

}
