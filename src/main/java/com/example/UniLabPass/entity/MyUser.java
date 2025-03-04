package com.example.UniLabPass.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class MyUser {
    @Id
    String id;
    @Column(unique = true, nullable = true, columnDefinition = "VARCHAR(255)")
    String email;
    String password;
    String firstName;
    String lastName;
    LocalDate dob;
    String gender;
    @Column(nullable = false)
    String verificationCode;
    @Column(nullable = false)
    Date expiryVerificationCode;
    @Column(nullable = false)
    boolean isVerified;

    @ManyToMany
    // Tự động tạo bảng user_role và tự động
    // thực hiện add dữ liệu vào bảng user_role
    // cũng như khi get và delete
    Set<Role> roles;
}
