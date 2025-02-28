package com.example.UniLabPass.entity;

import com.example.UniLabPass.compositekey.EventGuestKey;
import com.example.UniLabPass.compositekey.LabMemberKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class EventGuest {
    @EmbeddedId
    EventGuestKey eventGuestKey;
    String name;
}
