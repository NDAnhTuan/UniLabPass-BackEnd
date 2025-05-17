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
    @Schema(example = "2115177")
    String guestId;

    @Schema(example = "nguyenducanhtuan0602@gmail.com")
    String email;

    @Schema(example = "Bùi Phước Ban")
    String name;
}
