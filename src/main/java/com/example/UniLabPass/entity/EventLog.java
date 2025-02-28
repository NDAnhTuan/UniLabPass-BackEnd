package com.example.UniLabPass.entity;

import com.example.UniLabPass.enums.LogStatus;
import com.example.UniLabPass.enums.RecordType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String eventId;

    String guestId;

    @Column(nullable = false)
    RecordType recordType;

    LocalDateTime recordTime;

    LogStatus status;

    String photoURL;
}
