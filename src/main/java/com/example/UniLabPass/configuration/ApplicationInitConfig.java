package com.example.UniLabPass.configuration;

import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.enums.Role;
import com.example.UniLabPass.repository.MyUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//add logger
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    @Bean
    // Can run while app run
    ApplicationRunner applicationRunner(MyUserRepository myUserRepository) {
        return args -> {
            if (myUserRepository.findByEmail("admin").isEmpty()) {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());
                MyUser myUser = MyUser.builder()
                        .email("admin")
                        .password(passwordEncoder.encode("admin"))
                        .expiryVerificationCode(new Date())
                        .verificationCode("")
                        .isVerified(true)
                        //.roles(roles)
                        .build();
                myUserRepository.save(myUser);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
}
