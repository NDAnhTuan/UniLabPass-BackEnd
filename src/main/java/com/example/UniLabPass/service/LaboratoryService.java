package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LabCreationRequest;
import com.example.UniLabPass.dto.request.LabUpdateRequest;
import com.example.UniLabPass.dto.response.LabResponse;
import com.example.UniLabPass.entity.*;
import com.example.UniLabPass.enums.MemberStatus;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.LabMapper;
import com.example.UniLabPass.repository.*;
import com.example.UniLabPass.utils.GlobalUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LaboratoryService {
    MyUserRepository myUserRepository;
    LabRepository labRepository;
    LabMemberRepository labMemberRepository;
    RoleRepository roleRepository;
    LabEventRepository labEventRepository;
    LogRepository logRepository;

    LabMapper labMapper;

    LabEventService labEventService;
    GlobalUtils globalUtils;

    public LabResponse createLaboratory(LabCreationRequest request) {
        Lab lab = labMapper.toLab(request);
        try {
            lab = labRepository.save(lab);
        }
        catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.LAB_NAME_INVALID);
        }

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser myUser = myUserRepository.findByEmail(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        MyUser labAdmin = myUserRepository.findById(myUser.getId()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        LabMemberKey labMemberKey = new LabMemberKey(lab.getId(), labAdmin.getId());
        Role role = roleRepository.findById("MANAGER").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        LabMember labMember = LabMember.builder()
                .labMemberId(labMemberKey)
                .myUser(labAdmin)
                .lab(lab)
                .role(role)
                .memberStatus(MemberStatus.ACTIVE)
                .build();
        labMemberRepository.save(labMember);
        // Return lab's info
        return labMapper.toLabResponse(lab);
    }

    public LabResponse updateLaboratory(String labId, LabUpdateRequest request) {
        globalUtils.checkAuthorizeManager(labId);
        Lab lab = labRepository.findById(labId).orElseThrow(() -> new AppException(ErrorCode.LAB_NOT_EXISTED));
        labMapper.updateLab(lab, request);
        return labMapper.toLabResponse(labRepository.save(lab));
    }
    // Delete laboratory
    public void deleteLaboratory(String labId) throws IOException {
        // Check if user is MANAGER of lab
        globalUtils.checkAuthorizeManager(labId);

        // Delete all record of this lab
        logRepository.deleteByLabId(labId);

        // Delete all LabMember coexisted with this lab
        labMemberRepository.deleteByLabMemberId_LabId(labId);

        // Delete all event involve this lab
        List<LabEvent> events = labEventRepository.findAllByLabId(labId);
        for (LabEvent event : events) {
            labEventService.deleteEvent(event.getId());
        }

        // Delete lab
        labRepository.deleteById(labId);
    }

    // View all labs
    public List<LabResponse> getAllLabs() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        MyUser admin = myUserRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<LabResponse> labResponses = new ArrayList<LabResponse>();
        List<LabMember> labMemberResponses = labMemberRepository.findAllByLabMemberId_MyUserId(admin.getId());
        for (LabMember labMember : labMemberResponses) {
            LabResponse lab = LabResponse.builder()
                    .id(labMember.getLab().getId())
                    .name(labMember.getLab().getName())
                    .location(labMember.getLab().getLocation())
                    .capacity(labMember.getLab().getCapacity())
                    .build();
            labResponses.add(lab);
        }
        return labResponses;
    }
}
