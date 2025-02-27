package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LabMemberCreationRequest;
import com.example.UniLabPass.dto.request.LabMemberUpdateRequest;
import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.response.LabMemberInfoRespond;
import com.example.UniLabPass.dto.response.LabMemberResponse;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.entity.Lab;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.enums.Role;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LabMemberService {
    MyUserService myUserService;
    LogService logService;

    LabMemberRepository labMemberRepository;
    MyUserRepository myUserRepository;
    LabRepository labRepository;
    RoleRepository roleRepository;

    MyUserMapper myUserMapper;
    LabMemberMapper labMemberMapper;


    public void addLabMember(LabMemberCreationRequest request) {
        checkAuthorizeManager(request.getLabId());

        // If member is alreadty in this fking lab, throw error
        if (labMemberRepository.existsById(new LabMemberKey(request.getLabId(), request.getUserId()))) {
            throw new AppException(ErrorCode.MEMBER_ALREADY_EXISTED);
        }

        // if member not exist in database, then create a new one
        if (!myUserRepository.existsById(request.getUserId())) {
            MyUserCreationRequest myUserCreationRequest = MyUserCreationRequest.builder()
                    .id(request.getUserId())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .dob(request.getDob())
                    .gender(request.getGender())
                    .build();
            myUserService.createMyUser(myUserCreationRequest, Role.MEMBER);
        }
        else { // Else if member is already exist, then check if the info is as the same with request's data
            MyUser userCheck = myUserRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            if (! (userCheck.getId().equals(request.getUserId())
                    &   userCheck.getFirstName().equals(request.getFirstName())
                    &   userCheck.getLastName().equals(request.getLastName())
                    &   userCheck.getEmail().equals(request.getEmail())
                    &   userCheck.getDob().equals(request.getDob())
                    &   userCheck.getGender().equals(request.getGender()))) {
                throw new AppException(ErrorCode.USER_EXISTED);
            }
        }
        MyUser myUser = myUserRepository.findById(request.getUserId()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        // Increase lab capacity by 1
        // Don't create a clone lab when we cant find the lab we want
        Lab lab = labRepository.findById(request.getLabId()).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        // lab.setCapacity(lab.getCapacity() + 1);
        // labRepository.save(lab);

        LabMember labMember = new LabMember();
        labMember.setLabMemberId(new LabMemberKey(lab.getId(), myUser.getId()));
        labMember.setRole(roleRepository.findById(request.getRole()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED)));
        labMember.setLab(lab);
        labMember.setMyUser(myUser);
        labMemberRepository.save(labMember);
    }
    public List<LabMemberResponse> getLabMembers(String labId) {
        checkAuthorizeManager(labId);
        List<LabMember> labMemberList =  labMemberRepository.findAllByLabMemberId_LabId(labId).stream().toList();
        List<LabMemberResponse> labMemberResponses = new ArrayList<LabMemberResponse>();
        for (LabMember labMember : labMemberList) {
            if (labMember.getRole().getName().equals("MANAGER")) continue;
            // Mapping LabMember into LabMemberResponse
            LabMemberResponse labMemberResponse = LabMemberResponse.builder()
                    .id(labMember.getLabMemberId().getMyUserId())
                    .firstName(labMember.getMyUser().getFirstName())
                    .lastName(labMember.getMyUser().getLastName())
                    .gender(labMember.getMyUser().getGender())
                    .status(labMember.getMemberStatus())
                    .lastRecord(null) // Update when done all the logs
                    .build();

            labMemberResponses.add(labMemberResponse);
        }
        return labMemberResponses;
    }

    public LabMemberInfoRespond getLabMemberInfo(String labId, String memberId) {
        checkAuthorizeManager(labId);
        LabMemberKey labMemberKey = new LabMemberKey(labId, memberId);
        LabMember labMember = labMemberRepository.findById(labMemberKey).orElseThrow(() -> new AppException(ErrorCode.NO_RELATION));
        return LabMemberInfoRespond.builder()
                .myUserResponse(myUserMapper.toMyUserResponse(labMember.getMyUser()))
                .status(labMember.getMemberStatus())
                .build();
    }

    public void deleteLabMember(String labId, String userId) {
        checkAuthorizeManager(labId);
        LabMemberKey labMemberKey = new LabMemberKey(labId, userId);
        if (!labMemberRepository.existsById(labMemberKey)) {throw new AppException(ErrorCode.NO_RELATION);}
        labMemberRepository.deleteById(labMemberKey);

        // Delete all record involve with this user
        logService.deleteLog(labId, userId);

        // Check if this member is only in this lab, then delete it
        if (labMemberRepository.findAllByLabMemberId_MyUserId(userId).isEmpty()) {
            myUserService.deleteMyUser(userId);
        }

//        // Decrease lab capacity by 1
//        Lab lab = labRepository.findById(labId).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
//        lab.setCapacity(lab.getCapacity() - 1);
//        labRepository.save(lab);
    }

    public LabMemberResponse updateLabMember(LabMemberUpdateRequest request) {
        checkAuthorizeManager(request.getLabMemberKey().getLabId());
        LabMember labMember = labMemberRepository.findById(request.getLabMemberKey()).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        labMemberMapper.updateLabMember(labMember, request);

        // Mapping to LabMemberResponse
        return labMemberMapper.toLabMemberResponse(labMemberRepository.save(labMember));
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
