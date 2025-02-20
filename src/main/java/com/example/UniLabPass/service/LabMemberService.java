package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LabMemberCreationRequest;
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
        if (myUserRepository.findById(request.getUserId()).isEmpty()) {
            MyUserCreationRequest myUserCreationRequest = MyUserCreationRequest.builder()
                    .id(request.getUserId())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .build();
            myUserService.createMyUser(myUserCreationRequest, Role.MEMBER);

        }
        MyUser myUser = myUserRepository.findById(request.getUserId()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        com.example.UniLabPass.entity.Role role = roleRepository.findById(request.getRole()).orElse( new com.example.UniLabPass.entity.Role());
        Lab lab = labRepository.findById(request.getLabId()).orElse(new Lab());

        LabMember labMember = new LabMember();
        labMember.setLabMemberId(new LabMemberKey(lab.getId(), myUser.getId()));
        labMember.setRole(role);
        labMember.setLab(lab);
        labMember.setMyUser(myUser);
        labMember = labMemberRepository.save(labMember);

        return LabMemberResponse.builder()
                .labId(labMember.getLabMemberId().getLabId())
                .myUserResponse(myUserMapper.toMyUserResponse(labMember.getMyUser()))
                .role(labMember.getRole().getName())
                .memberStatus(labMember.getMemberStatus())
                .build();
    }
    public List<LabMemberResponse> getLabMembers(String labId) {
            List<LabMember> labMemberList =  labMemberRepository.findById_LabId(labId).stream().toList();
            List<LabMemberResponse> labMemberResponses = new ArrayList<LabMemberResponse>();
            for (LabMember labMember : labMemberList) {
                labMemberResponses.add(
                        LabMemberResponse.builder()
                        .labId(labMember.getLabMemberId().getLabId())
                        .myUserResponse(myUserMapper.toMyUserResponse(labMember.getMyUser()))
                        .role(labMember.getRole().getName())
                        .memberStatus(labMember.getMemberStatus())
                        .build()
                );
            }
            return labMemberResponses;
    }
}
