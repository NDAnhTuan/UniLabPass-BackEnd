package com.example.UniLabPass.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventWIthGuestCreationRequest {
    LabEventCreationRequest eventInfo;
    List<EventGuestCreationRequest> guestList;
}
