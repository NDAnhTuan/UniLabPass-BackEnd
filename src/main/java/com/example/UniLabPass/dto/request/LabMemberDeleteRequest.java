package com.example.UniLabPass.dto.request;

import com.example.UniLabPass.compositekey.LabMemberKey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabMemberDeleteRequest {
    LabMemberKey labMemberKey;
}
