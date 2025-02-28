package com.example.UniLabPass.dto.request;

import com.example.UniLabPass.compositekey.EventGuestKey;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventGuestUpdateRequest {
    EventGuestKey eventGuestKey;
    String guestName;
}
