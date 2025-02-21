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
public class LabMemberKey implements Serializable {
    @Schema(example = "123ab123")
    String labId;
    @Schema(example = "2115177")
    String myUserId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabMemberKey that = (LabMemberKey) o;
        return Objects.equals(labId, that.labId) && Objects.equals(myUserId, that.myUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labId, myUserId);
    }
}
