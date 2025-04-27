package com.example.UniLabPass.configuration;

import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.entity.Role;
import com.example.UniLabPass.repository.MyUserRepository;
import com.example.UniLabPass.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//add logger
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    @Bean
    // Can run while app run
    ApplicationRunner applicationRunner(MyUserRepository myUserRepository, RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("USER", "User is the role that can log into the account"));
                roleRepository.save(new Role("ADMIN", ""));
                roleRepository.save(new Role("GUEST", "Guests do not need to check their faces when checking in or checking out"));
                roleRepository.save(new Role("MANAGER", "The Manager has the right to control access to the laboratory"));
                roleRepository.save(new Role("MEMBER", "When checking in and checking out, members need to check their faces"));
                log.warn("Role Table has been create");
            }

            if (myUserRepository.findByEmail("admin@email.com").isEmpty()) {
                var roles = roleRepository.findById("ADMIN").map(List::of)  // Nếu có giá trị, chuyển thành List
                                                            .orElseGet(List::of); // Nếu rỗng, trả về List rỗng;
                MyUser myUser = MyUser.builder()
                        .email("admin@email.com")
                        .id(UUID.randomUUID().toString())
                        .password(passwordEncoder.encode("admin123"))
                        .isVerified(true)
                        .verificationCode("")
                        .expiryVerificationCode(new Date())
                        .roles(new HashSet<>(roles))
                        .build();
                myUserRepository.save(myUser);
                log.warn("admin@email.com user has been created with default password: admin123, please change it");
            }
        };
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
