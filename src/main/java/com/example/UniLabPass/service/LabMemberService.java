package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LabMemberCreationRequest;
import com.example.UniLabPass.dto.request.LabMemberDeleteRequest;
import com.example.UniLabPass.dto.request.LabMemberUpdateRequest;
import com.example.UniLabPass.dto.response.LabMemberResponse;
import com.example.UniLabPass.entity.Lab;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.entity.Role;
import com.example.UniLabPass.enums.MemberStatus;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.LabMemberMapper;
import com.example.UniLabPass.mapper.MyUserMapper;
import com.example.UniLabPass.repository.LabMemberRepository;
import com.example.UniLabPass.repository.LabRepository;
import com.example.UniLabPass.repository.MyUserRepository;
import com.example.UniLabPass.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LabMemberService {
    LabMemberRepository labMemberRepository;
    MyUserRepository myUserRepository;
    LabRepository labRepository;
    RoleRepository roleRepository;
    LabMemberMapper labMemberMapper;

    MyUserMapper myUserMapper;

    AdminService adminService;

    // Add member into lab
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public LabMemberResponse addLabMember(LabMemberCreationRequest request) {
        // Check if user is MANAGER of lab
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser admin = myUserRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!adminService.checkAdmin(request.getLabId(), admin.getId(), Arrays.asList("MANAGER", "ADMIN"))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // User handle
        MyUser user = new MyUser();
        if (myUserRepository.existsById(request.getUserId())) {
            user = myUserRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }
        else {
            // If user not exist, create a new user with email verified
//            user.setId(request.getUserId());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setDob(request.getDob());
            user.setPassword("123456"); // Automatic pass for new user
            user.setVerificationCode("1234");
            user.setExpiryVerificationCode(new Date(
                    Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli()
            ));
            user.setVerified(true);
            var roles = roleRepository.findById(request.getRole()).map(List::of)  // Nếu có giá trị, chuyển thành List
                    .orElseGet(List::of); // Nếu rỗng, trả về List rỗng;
            user.setRoles(new HashSet<>(roles));
        }

        try {
            user = myUserRepository.save(user);
        }
        catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Others
        Lab lab = labRepository.findById(request.getLabId()).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        Role role = roleRepository.findById(request.getRole()).orElseThrow(() ->  new AppException(ErrorCode.USERNAME_INVALID));
        LabMemberKey labMemberKey = new LabMemberKey(user.getId(), lab.getId());
        LabMember labMember = LabMember.builder()
                .labMemberId(labMemberKey)
                .myUser(user)
                .lab(lab)
                .role(role)
                .memberStatus(MemberStatus.ACTIVE)
                .build();

        // Increase lab capacity by 1
        lab.setCapacity(lab.getCapacity() + 1);
        labRepository.save(lab);

        // Return result
        return labMemberMapper.toLabMemberResponse(labMemberRepository.save(labMember));
    }

    // Delete member form lab
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteLabMember(LabMemberDeleteRequest request) {
        // Check if user is MANAGER of lab
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser admin = myUserRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!adminService.checkAdmin(request.getLabMemberKey().getLabId(), admin.getId(), Arrays.asList("MANAGER", "ADMIN"))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Delete user in LabMemberKey
        LabMemberKey labMemberKey = new LabMemberKey(request.getLabMemberKey().getLabId(), request.getLabMemberKey().getMyUserId());
        if (!labMemberRepository.existsById(labMemberKey)) {throw new AppException(ErrorCode.NO_RELATION);}
        labMemberRepository.deleteById(labMemberKey);

        // Decrease lab capacity by 1
        Lab lab = labRepository.findById(request.getLabMemberKey().getLabId()).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        lab.setCapacity(lab.getCapacity() - 1);
        labRepository.save(lab);
    }

    // Get list of member from lab
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<LabMember> getAllLabMembers(String labId) {
        // Check if user is MANAGER of lab
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser admin = myUserRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!adminService.checkAdmin(labId, admin.getId(), Arrays.asList("MANAGER", "ADMIN"))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return labMemberRepository.findByLabMemberId_LabId(labId);
    }

    // Update lab member's status (ACTIVE OR BLOCKED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public LabMemberResponse updateLabMember(LabMemberUpdateRequest request) {
        // Check if user is MANAGER of lab
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser admin = myUserRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!adminService.checkAdmin(request.getLabMemberKey().getLabId(), admin.getId(), Arrays.asList("MANAGER", "ADMIN"))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        LabMember labMember = labMemberRepository.findById(request.getLabMemberKey()).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        labMemberMapper.updateLabMember(labMember, request);

        return labMemberMapper.toLabMemberResponse(labMemberRepository.save(labMember));
    }

}