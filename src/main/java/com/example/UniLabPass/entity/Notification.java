package com.example.UniLabPass.entity;

import com.example.UniLabPass.enums.NotifyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Notification {
    @Id
    String id;
    String userId;
    String title;
    String body;
    NotifyType type;
    @CreatedDate
    Date createdAt;

}
