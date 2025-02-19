package com.example.UniLabPass.service;

import com.example.UniLabPass.dto.request.LabMemberDeleteRequest;
import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.request.MyUserUpdateRequest;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.enums.Role;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.MyUserMapper;
import com.example.UniLabPass.repository.LabMemberRepository;
import com.example.UniLabPass.repository.MyUserRepository;
import com.example.UniLabPass.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class MyUserService {
    MyUserRepository myUserRepository;
    RoleRepository roleRepository;
    LabMemberRepository labMemberRepository;
    MyUserMapper myUserMapper;
    PasswordEncoder passwordEncoder;
    LabMemberService labMemberService;
    EmailService emailService;

    @NonFinal
    static final String CHARACTERS = "1234567890";
    @NonFinal
    static final int CODE_LENGTH = 4;
    @NonFinal
    static final SecureRandom random = new SecureRandom();


    public MyUserResponse createMyUser(MyUserCreationRequest request) {
        MyUser myUser = myUserMapper.toMyUser(request);
        myUser.setPassword(passwordEncoder.encode(request.getPassword()));
        myUser.setVerificationCode(generateVerificationCode());
        myUser.setExpiryVerificationCode(new Date(
                Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli()
        ));
        var roles = roleRepository.findById("USER").map(List::of)  // Nếu có giá trị, chuyển thành List
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

    // Every user is able to get their own info
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public MyUserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        MyUser myUser = myUserRepository.findByEmail(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        return myUserMapper.toMyUserResponse(myUser);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public MyUserResponse updateMyUser(String userId,MyUserUpdateRequest request) {
        MyUser myUser = myUserRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        myUserMapper.updateMyUser(myUser, request);
        myUser.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        myUser.setRoles(new HashSet<>(roles));

        return myUserMapper.toMyUserResponse(myUserRepository.save(myUser));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteMyUser(String userId) {
        // Delete all LabMember associated with this user & reduce capacity of the lab has this user
        List<LabMember> labMembers = labMemberRepository.findByLabMemberId_MyUserId(userId);
        for (LabMember labMember : labMembers) {
            LabMemberDeleteRequest deleteRequest = new LabMemberDeleteRequest(labMember.getLabMemberId());
            labMemberService.deleteLabMember(deleteRequest);
        }

        // Delete this user
        myUserRepository.deleteById(userId);
    }
    //Protect Method before access
    // hasRole = Role_?
//    @PreAuthorize("hasRole('ADMIN')")
    // hasAuthority = full Scope name
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<MyUserResponse> getMyUsers() {
        return myUserRepository.findAll().stream().map(myUserMapper::toMyUserResponse).toList();
    }
//    @PostAuthorize("returnObject.username == authentication.name")
    // Cho thuc thi nhung khong tra ve ket qua (return)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public MyUserResponse getMyUser(String id) {
        return myUserMapper.toMyUserResponse(
                myUserRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED))
        );
    }

    private  String generateVerificationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}
