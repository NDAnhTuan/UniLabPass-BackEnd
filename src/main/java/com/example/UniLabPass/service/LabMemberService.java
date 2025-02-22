package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LabMemberCreationRequest;
import com.example.UniLabPass.dto.request.LabMemberUpdateRequest;
import com.example.UniLabPass.dto.request.MyUserCreationRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LabMemberService {
    MyUserService myUserService;
    LabMemberRepository labMemberRepository;
    MyUserRepository myUserRepository;
    LabRepository labRepository;
    RoleRepository roleRepository;
    MyUserMapper myUserMapper;
    LabMemberMapper labMemberMapper;


    public LabMemberResponse addLabMember(LabMemberCreationRequest request) {
        checkAuthorizeManager(request.getLabId());
        if (myUserRepository.findById(request.getUserId()).isEmpty()) {
            MyUserCreationRequest myUserCreationRequest = MyUserCreationRequest.builder()
                    .id(request.getUserId())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .dob(request.getDob())
                    .build();
            myUserService.createMyUser(myUserCreationRequest, Role.MEMBER);

        }
        MyUser myUser = myUserRepository.findById(request.getUserId()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        // Increase lab capacity by 1
        Lab lab = labRepository.findById(request.getLabId()).orElse(new Lab());
        lab.setCapacity(lab.getCapacity() + 1);
        labRepository.save(lab);

        LabMember labMember = new LabMember();
        labMember.setLabMemberId(new LabMemberKey(lab.getId(), myUser.getId()));
        labMember.setRole(roleRepository.findById(request.getRole()).orElseThrow());
        labMember.setLab(lab);
        labMember.setMyUser(myUser);
        labMember = labMemberRepository.save(labMember);

        LabMemberResponse labMemberResponse = labMemberMapper.toLabMemberResponse(labMember);
        labMemberResponse.setMyUserResponse(myUserMapper.toMyUserResponse(labMember.getMyUser()));
        return labMemberResponse;
    }
    public List<LabMemberResponse> getLabMembers(String labId) {
        checkAuthorizeManager(labId);
        List<LabMember> labMemberList =  labMemberRepository.findByLabMemberId_LabId(labId).stream().toList();
        List<LabMemberResponse> labMemberResponses = new ArrayList<LabMemberResponse>();
        for (LabMember labMember : labMemberList) {
            LabMemberResponse labMemberResponse = labMemberMapper.toLabMemberResponse(labMember);
            labMemberResponse.setMyUserResponse(myUserMapper.toMyUserResponse(labMember.getMyUser()));

            labMemberResponses.add(labMemberResponse);
        }
        return labMemberResponses;
    }
    public void deleteLabMember(String labId, String userId) {
        checkAuthorizeManager(labId);
        LabMemberKey labMemberKey = new LabMemberKey(labId, userId);
        if (!labMemberRepository.existsById(labMemberKey)) {throw new AppException(ErrorCode.NO_RELATION);}
        labMemberRepository.deleteById(labMemberKey);

        // Decrease lab capacity by 1
        Lab lab = labRepository.findById(labId).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        lab.setCapacity(lab.getCapacity() - 1);
        labRepository.save(lab);
    }

    public LabMemberResponse updateLabMember(LabMemberUpdateRequest request) {
        checkAuthorizeManager(request.getLabMemberKey().getLabId());
        LabMember labMember = labMemberRepository.findById(request.getLabMemberKey()).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        labMemberMapper.updateLabMember(labMember, request);
        return labMemberMapper.toLabMemberResponse(labMemberRepository.save(labMember));
    }

    public void checkAuthorizeManager(String labId) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser manager = myUserRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        LabMember managerUser = labMemberRepository.findById(new LabMemberKey(labId,manager.getId())).orElseThrow(
                () -> new AppException(ErrorCode.MEMBER_NOT_EXISTED)
        );
        log.info("Manager Role: " +  managerUser.getRole().getName());
        if (!managerUser.getRole().getName().equals("MANAGER")) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

}
