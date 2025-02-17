package com.example.UniLabPass.entity;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class LabMember {
    @EmbeddedId
    LabMemberKey labMemberId;

    @ManyToOne

    @JoinColumn(name = "my_user_id")
    @MapsId("myUserId")
    MyUser myUser;

    @ManyToOne(targetEntity = Lab.class)
    @MapsId("labId")
    @JoinColumn(name = "lab_id")
    Lab lab;

    @ManyToOne
    @JoinColumn(name = "role_name")
    Role role;

    @Column(nullable = false)
    MemberStatus memberStatus;

}
