package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.repository.LabMemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminService {
    LabMemberRepository labMemberRepository;
    private final AbstractHandlerMethodAdapter abstractHandlerMethodAdapter;

    // Check if a user is lab's manager
    public boolean checkAdmin(String labId, String adminId, List<String> roles) {
        LabMemberKey key = new LabMemberKey(labId, adminId);
        LabMember labMember = labMemberRepository.findById(key).orElseThrow(() -> new AppException(ErrorCode.NO_RELATION));
        if (!roles.contains(labMember.getRole().getName())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return true;
    }
}
