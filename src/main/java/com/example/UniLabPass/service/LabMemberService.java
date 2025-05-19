package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.InviteManagerForLabRequest;
import com.example.UniLabPass.dto.request.LabMemberCreationRequest;
import com.example.UniLabPass.dto.request.LabMemberUpdateRequest;
import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.response.LabMemberInfoRespond;
import com.example.UniLabPass.dto.response.LabMemberResponse;
import com.example.UniLabPass.entity.*;
import com.example.UniLabPass.enums.MemberStatus;
import com.example.UniLabPass.enums.NotifyType;
import com.example.UniLabPass.enums.Role;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.LabMemberMapper;
import com.example.UniLabPass.mapper.MyUserMapper;
import com.example.UniLabPass.repository.*;
import com.example.UniLabPass.utils.AESEncryptionUtil;
import com.example.UniLabPass.utils.GlobalUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LabMemberService {
    MyUserService myUserService;
    LogService logService;
    ExpoPushService expoPushService;
    EmailService emailService;
    QrCodeService qrCodeService;

    LabMemberRepository labMemberRepository;
    MyUserRepository myUserRepository;
    LabRepository labRepository;
    RoleRepository roleRepository;
    NotificationRepository notificationRepository;

    MyUserMapper myUserMapper;
    LabMemberMapper labMemberMapper;
    LogRepository logRepository;

    GlobalUtils globalUtils;
    AESEncryptionUtil aesEncryptionUtil;
    @NonFinal
    @Value("${app.Global.RemainVerify}")
    int RemainVerify;


    public void addLabMember(LabMemberCreationRequest request, MultipartFile file) throws Exception {
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

            // Send Qr Code
//            String encodedUserId = aesEncryptionUtil.encrypt(request.getUserId());
//            byte[] qr = qrCodeService.generateQRCode(encodedUserId, 250, 250);
//            emailService.sendQRCode(request.getEmail(),qr);
        }
        else { // Else if member is already exist, then check if the info is as the same with request's data
            MyUser userCheck = myUserRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                // Check email
            if (!userCheck.getEmail().equals(request.getEmail())) {
                // Check other info
                throw new AppException(ErrorCode.USER_ID_EXISTED);
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
        labMember.setRemainVerify(RemainVerify);
        labMember.setExpiryRemain(LocalDateTime.now());
        labMemberRepository.save(labMember);
    }

    public String inviteManager(InviteManagerForLabRequest request) {
        globalUtils.checkAuthorizeManager(request.getLabId());
        String currEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        MyUser myUser = myUserRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        Lab lab = labRepository.findById(request.getLabId()).orElseThrow(
                () -> new AppException(ErrorCode.LAB_NOT_EXISTED)
        );
        labMemberRepository.save(LabMember.builder()
                        .labMemberId(new LabMemberKey(lab.getId(), myUser.getId()))
                        .myUser(myUser)
                        .lab(lab)
                        .role(roleRepository.findById("PREMANAGER").orElseThrow(
                                () -> new AppException(ErrorCode.ROLE_NOT_EXISTED)
                        ))
                .build());
        expoPushService.sendPushNotification(
                myUser.getExpoPushToken(),
                "Invite to become lab manager of " + lab.getName(),
                currEmail + " invited you to become lab manager.");
        notificationRepository.save(Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(myUser.getId())
                .title("Invite to become lab manager of " + lab.getName())
                .body(currEmail + " invited you to become lab manager.")
                .type(NotifyType.YES_NO)
                .labId(request.getLabId())
                .createdAt(new Date())
                .build());
        return "Invite successfully";
    }

    public LabMemberResponse acceptInvite(String labId) {
        String currEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        MyUser myUser = myUserRepository.findByEmail(currEmail).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        LabMember labMember = labMemberRepository.findById(new LabMemberKey(labId,myUser.getId())).orElseThrow(
                () -> new AppException(ErrorCode.MEMBER_NOT_EXISTED)
        );
        if (!labMember.getRole().getName().equals("PREMANAGER"))
            throw new AppException(ErrorCode.NOT_PREMANAGER);
        labMember.setMemberStatus(MemberStatus.ACTIVE);
        labMember.setRole(roleRepository.findById("MANAGER").orElseThrow(
                () -> new AppException(ErrorCode.ROLE_NOT_EXISTED)
        ));
        labMember.setRemainVerify(RemainVerify);
        labMember.setExpiryRemain(LocalDateTime.now());
        labMember = labMemberRepository.save(labMember);
        return labMemberMapper.toLabMemberResponse(labMember);
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

    public LabMemberInfoRespond getLabMemberInfo(String labId, String qrCode, boolean isQrCode) throws Exception {
        globalUtils.checkAuthorizeManager(labId);
//        var memberId = isQrCode ? aesEncryptionUtil.decrypt(qrCode) : qrCode;
        var memberId = qrCode;
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
        var myUser = labMemberRepository.findById(labMemberKey).orElseThrow(
                () -> new AppException(ErrorCode.NO_RELATION)
        ).getMyUser();
        labMemberRepository.deleteById(labMemberKey);

        // Delete all record involve with this user
        logService.deleteLog(labId, userId);

        // Check if this member is only in this lab, then delete it
        if (labMemberRepository.findAllByLabMemberId_MyUserId(userId).isEmpty()
                && !myUser.getRoles().stream().anyMatch(role -> "USER".equals(role.getName()))) {
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
