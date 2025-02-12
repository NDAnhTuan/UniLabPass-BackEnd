package com.example.UniLabPass.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorApiResponse {
    @Schema(description = "Response code", example = "1001")
    private int code;

    @Schema(description = "Error message", example = "User not found")
    private String message;
}
