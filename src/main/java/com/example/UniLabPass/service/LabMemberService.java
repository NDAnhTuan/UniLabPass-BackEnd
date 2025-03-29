package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LabMemberCreationRequest;
import com.example.UniLabPass.dto.request.LabMemberUpdateRequest;
import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.response.LabMemberInfoRespond;
import com.example.UniLabPass.dto.response.LabMemberResponse;
import com.example.UniLabPass.entity.Lab;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.LaboratoryLog;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.enums.Role;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.LabMemberMapper;
import com.example.UniLabPass.mapper.MyUserMapper;
import com.example.UniLabPass.repository.*;
import com.example.UniLabPass.utils.GlobalUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
    LogRepository logRepository;

    GlobalUtils globalUtils;


    public void addLabMember(LabMemberCreationRequest request, MultipartFile file) throws IOException {
        globalUtils.checkAuthorizeManager(request.getLabId());

        // If member is already in this fking lab, throw error
        if (labMemberRepository.existsById(new LabMemberKey(request.getLabId(), request.getUserId()))) {
            throw new AppException(ErrorCode.MEMBER_ALREADY_EXISTED);
        }

        // if member not exist in database, then first check email to see if it already in the database or not, then create a new one
        if (!myUserRepository.existsById(request.getUserId())) {
            // Check email
            if (myUserRepository.existsByEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            MyUserCreationRequest myUserCreationRequest = MyUserCreationRequest.builder()
                    .id(request.getUserId())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .dob(request.getDob())
                    .gender(request.getGender())
                    .build();
            myUserService.createMyUser(myUserCreationRequest, Role.MEMBER, file);
        }
        else { // Else if member is already exist, then check if the info is as the same with request's data
            MyUser userCheck = myUserRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            if (userCheck.getId().equals(request.getUserId())) {
                // Check email
                if (userCheck.getEmail().equals(request.getEmail())) {
                    // Check other info
                    if (! (userCheck.getFirstName().equals(request.getFirstName())
                        && userCheck.getLastName().equals(request.getLastName())
                        && userCheck.getDob().equals(request.getDob())
                        && userCheck.getGender().equals(request.getGender())) ) {
                        throw new AppException(ErrorCode.FALSE_USER_DATA);
                    }
                }
                else {
                    throw new AppException(ErrorCode.USER_ID_EXISTED);
                }
            }
        }
        MyUser myUser = myUserRepository.findById(request.getUserId()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        // Increase lab capacity by 1
        // Don't create a clone lab when we cant find the lab we want
        Lab lab = labRepository.findById(request.getLabId()).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));

        LabMember labMember = new LabMember();
        labMember.setLabMemberId(new LabMemberKey(lab.getId(), myUser.getId()));
        labMember.setRole(roleRepository.findById(request.getRole()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED)));
        labMember.setLab(lab);
        labMember.setMyUser(myUser);
        labMemberRepository.save(labMember);
    }
    public List<LabMemberResponse> getLabMembers(String labId) {
        globalUtils.checkAuthorizeManager(labId);
        List<LabMember> labMemberList = labMemberRepository.findAllByLabMemberId_LabId(labId).stream().toList();
        List<LabMemberResponse> labMemberResponses = new ArrayList<LabMemberResponse>();
        for (LabMember labMember : labMemberList) {
            if (labMember.getRole().getName().equals("MANAGER")) continue;
            // Mapping LabMember into LabMemberResponse
            LaboratoryLog log = logRepository.findFirstByUserIdAndLabIdOrderByRecordTimeDesc(
                    labMember.getMyUser().getId(), labMember.getLab().getId()
            ).orElse(null);
            LocalDateTime userLastRecord = null;
            if (log != null) {
                userLastRecord = log.getRecordTime();
            }

            LabMemberResponse labMemberResponse = LabMemberResponse.builder()
                    .id(labMember.getLabMemberId().getMyUserId())
                    .firstName(labMember.getMyUser().getFirstName())
                    .lastName(labMember.getMyUser().getLastName())
                    .gender(labMember.getMyUser().getGender())
                    .status(labMember.getMemberStatus())
                    .lastRecord(userLastRecord) // Update when done all the logs
                    .build();

            labMemberResponses.add(labMemberResponse);
        }
        return labMemberResponses;
    }

    public LabMemberInfoRespond getLabMemberInfo(String labId, String memberId) {
        globalUtils.checkAuthorizeManager(labId);
        LabMemberKey labMemberKey = new LabMemberKey(labId, memberId);
        LabMember labMember = labMemberRepository.findById(labMemberKey).orElseThrow(() -> new AppException(ErrorCode.NO_RELATION));
        return LabMemberInfoRespond.builder()
                .myUserResponse(myUserMapper.toMyUserResponse(labMember.getMyUser()))
                .status(labMember.getMemberStatus())
                .build();
    }

    public void deleteLabMember(String labId, String userId) throws IOException {
        globalUtils.checkAuthorizeManager(labId);
        LabMemberKey labMemberKey = new LabMemberKey(labId, userId);
        if (!labMemberRepository.existsById(labMemberKey)) {throw new AppException(ErrorCode.NO_RELATION);}
        labMemberRepository.deleteById(labMemberKey);

        // Delete all record involve with this user
        logService.deleteLog(labId, userId);

        // Check if this member is only in this lab, then delete it
        if (labMemberRepository.findAllByLabMemberId_MyUserId(userId).isEmpty()) {
            myUserService.deleteMyUser(userId);
        }

    }

    public LabMemberResponse updateLabMember(LabMemberUpdateRequest request) {
        globalUtils.checkAuthorizeManager(request.getLabMemberKey().getLabId());
        LabMember labMember = labMemberRepository.findById(request.getLabMemberKey()).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        labMemberMapper.updateLabMember(labMember, request);

        // Mapping to LabMemberResponse
        return labMemberMapper.toLabMemberResponse(labMemberRepository.save(labMember));
    }



}
