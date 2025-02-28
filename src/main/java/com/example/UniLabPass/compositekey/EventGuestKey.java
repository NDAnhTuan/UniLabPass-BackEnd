package com.example.UniLabPass.compositekey;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
@EqualsAndHashCode
public class EventGuestKey implements Serializable {
    @Schema(example = "123ab123")
    String eventId;
    @Schema(example = "2115177")
    String guestId;
}
