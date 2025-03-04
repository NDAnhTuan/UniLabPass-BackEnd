package com.example.UniLabPass.service;

import com.example.UniLabPass.dto.request.LabMemberCreationRequest;
import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.request.MyUserUpdateRequest;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.enums.Role;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.MyUserMapper;
import com.example.UniLabPass.repository.MyUserRepository;
import com.example.UniLabPass.repository.RoleRepository;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class MyUserService {
    MyUserRepository myUserRepository;
    RoleRepository roleRepository;
    MyUserMapper myUserMapper;
    PasswordEncoder passwordEncoder;

    EmailService emailService;


    public MyUserResponse createMyUser(MyUserCreationRequest request, Role role) {
        MyUser myUser = myUserMapper.toMyUser(request);
        myUser.setId(request.getId() != null ? request.getId() : UUID.randomUUID().toString());

        if (role.equals(Role.USER)) {
            myUser.setPassword(passwordEncoder.encode(request.getPassword()));
            myUser.setVerificationCode(emailService.generateVerificationCode());
            myUser.setExpiryVerificationCode(new Date(
                    Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli()
            ));
            var roles = roleRepository.findById(role.name()).map(List::of)  // Nếu có giá trị, chuyển thành List
                    .orElseGet(List::of); // Nếu rỗng, trả về List rỗng;
            myUser.setRoles(new HashSet<>(roles));

        }
        // Trường hợp không phải tạo tài khoản
        else {
            myUser.setVerificationCode("");
            myUser.setExpiryVerificationCode(new Date());
            myUser.setRoles(new HashSet<>());
        }
        try {
            myUser = myUserRepository.save(myUser);
        }
        catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (role.equals(Role.USER)) emailService.sendVerificationEmail(myUser.getEmail(), myUser.getVerificationCode());
        return myUserMapper.toMyUserResponse(myUser);
    }

    public MyUserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        MyUser myUser = myUserRepository.findByEmail(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        return myUserMapper.toMyUserResponse(myUser);

    }
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public MyUserResponse updateMyUser(String userId,MyUserUpdateRequest request) {
        MyUser myUser = myUserRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        myUserMapper.updateMyUser(myUser, request);
        if (request.getPassword() != null && !request.getPassword().isEmpty())
            myUser.setPassword(passwordEncoder.encode(request.getPassword()));
        var roles = request.getRoles() != null ?
                roleRepository.findAllById(request.getRoles()) : new ArrayList<com.example.UniLabPass.entity.Role>();
        myUser.setRoles(new HashSet<>(roles));

        return myUserMapper.toMyUserResponse(myUserRepository.save(myUser));
    }
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteMyUser(String userId) {
        myUserRepository.deleteById(userId);
    }
    //Protect Method before access
    // hasRole = Role_?
//    @PreAuthorize("hasRole('ADMIN')")
    // hasAuthority = full Scope name
    public List<MyUserResponse> getMyUsers() {
        return myUserRepository.findAll().stream().map(myUserMapper::toMyUserResponse).toList();
    }
//    @PostAuthorize("returnObject.username == authentication.name")
    // Cho thuc thi nhung khong tra ve ket qua (return)
    public MyUserResponse getMyUser(String id) {
        return myUserMapper.toMyUserResponse(
                myUserRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED))
        );
    }


}
