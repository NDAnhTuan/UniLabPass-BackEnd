package com.example.UniLabPass.utils;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.repository.LabMemberRepository;
import com.example.UniLabPass.repository.MyUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GlobalUtils {
    MyUserRepository myUserRepository;
    LabMemberRepository labMemberRepository;
    @NonFinal
    static final String CHARACTERS = "1234567890";
    @NonFinal
    static final int CODE_LENGTH = 4;
    @NonFinal
    static final SecureRandom random = new SecureRandom();
    public String generateVerificationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    public void checkAuthorizeManager(String labId) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser manager = myUserRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        LabMember managerUser = labMemberRepository.findById(new LabMemberKey(labId,manager.getId())).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHORIZED)
        );
        log.info("Manager Role: " +  managerUser.getRole().getName());
        if (!managerUser.getRole().getName().equals("MANAGER")) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
