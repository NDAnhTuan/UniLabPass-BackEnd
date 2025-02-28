package com.example.UniLabPass.dto.request;

import com.example.UniLabPass.compositekey.EventGuestKey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventGuestCreationRequest {
    @Schema(example = "2112843")
    String guestId;

    @Schema(example = "Bùi Phước Ban")
    String name;
}
