package com.example.UniLabPass.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationCodeRequest {
    @Schema(example = "nguyenducanhtuan0602@gmail.com", required = true)
    String email;
    @Schema(description = "6-digit code",example = "123456", required = true)
    String code;
}
