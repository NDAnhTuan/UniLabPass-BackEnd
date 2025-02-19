package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LabMemberCreationRequest;
import com.example.UniLabPass.dto.request.LabMemberDeleteRequest;
import com.example.UniLabPass.dto.request.LabMemberUpdateRequest;
import com.example.UniLabPass.dto.response.LabMemberResponse;
import com.example.UniLabPass.entity.Lab;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.enums.MemberStatus;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.LabMemberMapper;
import com.example.UniLabPass.repository.LabMemberRepository;
import com.example.UniLabPass.repository.LabRepository;
import com.example.UniLabPass.repository.MyUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LabMemberService {
    LabMemberRepository labMemberRepository;
    MyUserRepository myUserRepository;
    LabRepository labRepository;
    LabMemberMapper labMemberMapper;

    // Add member into lab
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public LabMemberResponse addLabMember(LabMemberCreationRequest request) {
        // Create relatonship
        MyUser user = myUserRepository.findById(request.getMemberKey().getMyUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Lab lab = labRepository.findById(request.getMemberKey().getLabId()).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        LabMemberKey labMemberKey = new LabMemberKey(user.getId(), lab.getId());
        LabMember labMember = LabMember.builder()
                .labMemberId(labMemberKey)
                .myUser(user)
                .lab(lab)
                .role(request.getRole())
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
        LabMemberKey labMemberKey = new LabMemberKey(request.getLabMemberKey().getLabId(), request.getLabMemberKey().getMyUserId());
        LabMember labMember = labMemberRepository.findById(labMemberKey).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        labMemberRepository.deleteById(labMemberKey);

        // Decrease lab capacity by 1
        Lab lab = labRepository.findById(request.getLabMemberKey().getLabId()).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        lab.setCapacity(lab.getCapacity() - 1);
        labRepository.save(lab);
    }

    // Get list of member from lab
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<LabMember> getAllLabMembers(String labId) {
        return labMemberRepository.findByLabMemberId_LabId(labId);
    }

    // Update lab member's status (ACTIVE OR BLOCKED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public LabMemberResponse updateLabMember(LabMemberUpdateRequest request) {
        LabMember labMember = labMemberRepository.findById(request.getLabMemberId()).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        labMemberMapper.updateLabMember(labMember, request);

        return labMemberMapper.toLabMemberResponse(labMemberRepository.save(labMember));
    }

}