package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LabCreationRequest;
import com.example.UniLabPass.dto.request.LabUpdateRequest;
import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.response.LabResponse;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.entity.Lab;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.entity.Role;
import com.example.UniLabPass.enums.MemberStatus;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.LabMapper;
import com.example.UniLabPass.mapper.MyUserMapper;
import com.example.UniLabPass.repository.LabMemberRepository;
import com.example.UniLabPass.repository.LabRepository;
import com.example.UniLabPass.repository.MyUserRepository;
import com.example.UniLabPass.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LaboratoryService {
    MyUserRepository myUserRepository;
    LabRepository labRepository;
    LabMemberRepository labMemberRepository;
    RoleRepository roleRepository;
    MyUserMapper myUserMapper;
    LabMapper labMapper;
    PasswordEncoder passwordEncoder;

    EmailService emailService;
    AdminService adminService;

    @NonFinal
    static final String CHARACTERS = "1234567890";
    @NonFinal
    static final int CODE_LENGTH = 4;
    @NonFinal
    static final SecureRandom random = new SecureRandom();
    
    // Create user with ADMIN role (since there are no create API for LabAdmin)
    public MyUserResponse createLabAdmin(MyUserCreationRequest request) {
        MyUser myUser = myUserMapper.toMyUser(request);
        myUser.setPassword(passwordEncoder.encode(request.getPassword()));
        myUser.setVerificationCode(generateVerificationCode());
        myUser.setExpiryVerificationCode(new Date(
                Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli()
        ));
        var roles = roleRepository.findById("ADMIN").map(List::of)  // Nếu có giá trị, chuyển thành List
                .orElseGet(List::of); // Nếu rỗng, trả về List rỗng;
        myUser.setRoles(new HashSet<>(roles));

        try {
            myUser = myUserRepository.save(myUser);
        }
        catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        emailService.sendVerificationEmail(myUser.getEmail(), myUser.getVerificationCode());

        return myUserMapper.toMyUserResponse(myUser);
    }

    // Create new laboratory and add ADMIN to LabMember
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public LabResponse createLaboratory(LabCreationRequest request) {
        // Create new lab
        Lab lab = labMapper.toLab(request);
        lab.setCapacity(0);
        Lab savedLab;
        try {
            savedLab = labRepository.save(lab);
        }
        catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.LAB_NAME_INVALID);
        }

        // Add new relationship for LabMember
        // - Find AdminID through token
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser admin = myUserRepository.findByEmail(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        MyUser labAdmin = myUserRepository.findById(admin.getId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        LabMemberKey labMemberKey = new LabMemberKey(savedLab.getId(), labAdmin.getId());
        Role role = roleRepository.findById("MANAGER").orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        LabMember labMember = LabMember.builder()
                .labMemberId(labMemberKey)
                .myUser(labAdmin)
                .lab(savedLab)
                .role(role)
                .memberStatus(MemberStatus.ACTIVE)
                .build();
        labMemberRepository.save(labMember);

        // Return lab's info
        return labMapper.toLabResponse(savedLab);
    }

    // Update laboratory
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public LabResponse updateLaboratory(String labId, LabUpdateRequest request) {
        // Check if user is MANAGER of lab
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser admin = myUserRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (adminService.checkAdmin(labId, admin.getId(), Arrays.asList("MANAGER"))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Lab lab = labRepository.findById(labId).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        labMapper.updateLab(lab, request);
        return labMapper.toLabResponse(labRepository.save(lab));
    };

    // Delete laboratory
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteLaboratory(String labId) {
        // Check if user is MANAGER of lab
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser admin = myUserRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!adminService.checkAdmin(labId, admin.getId(), Arrays.asList("MANAGER"))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Delete all LabMember coexisted with this lab
        labMemberRepository.deleteByLabMemberId_LabId(labId);

        // Delete lab
        Lab lab = labRepository.findById(labId).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        labRepository.deleteById(labId);
    }

    // View all labs
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<LabMember> getAllLabs() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser admin = myUserRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return labMemberRepository.findByLabMemberId_MyUserId(admin.getId());
    }

    private String generateVerificationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}
