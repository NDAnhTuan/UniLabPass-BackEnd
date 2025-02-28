package com.example.UniLabPass.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class LabEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String labId;
    String name;
    LocalDateTime startTime;
    LocalDateTime endTime;
}
