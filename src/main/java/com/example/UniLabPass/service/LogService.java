package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LogCreationRequest;
import com.example.UniLabPass.dto.response.DailyReportResponse;
import com.example.UniLabPass.dto.response.LogDetailRespond;
import com.example.UniLabPass.dto.response.LogRespond;
import com.example.UniLabPass.dto.response.WeeklyReportResponse;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.LaboratoryLog;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.entity.Notification;
import com.example.UniLabPass.enums.*;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.LogMapper;
import com.example.UniLabPass.repository.LabMemberRepository;
import com.example.UniLabPass.repository.LogRepository;
import com.example.UniLabPass.repository.MyUserRepository;
import com.example.UniLabPass.repository.NotificationRepository;
import com.example.UniLabPass.utils.GlobalUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LogService {
    MyUserRepository myUserRepository;
    LabMemberRepository labMemberRepository;
    LogRepository logRepository;
    NotificationRepository notificationRepository;

    CloudinaryService cloudinaryService;
    ExpoPushService expoPushService;

    LogMapper logMapper;

    GlobalUtils globalUtils;
    @NonFinal
    @Value("${app.Global.VNHour}")
    int VNHour;

    // Add a new log
    public LogRespond addNewLog(LogCreationRequest request, MultipartFile file) throws IOException {
        if (request.getLabId() == null
                || request.getUserId() == null
                || request.getRecordType() == null) {
            throw new AppException(ErrorCode.LOG_CREATE_ERROR);
        }
        LocalDateTime now = LocalDateTime.now().plusHours(VNHour);

        LaboratoryLog newRecord = logMapper.toLaboratoryLog(request);
        newRecord.setRecordTime(now);

        LaboratoryLog recentLog = logRepository
                .findFirstByUserIdAndLabIdAndStatusOrderByRecordTimeDesc(
                        newRecord.getUserId(), newRecord.getLabId(), LogStatus.SUCCESS)
                .orElse(null);
        LabMember member = labMemberRepository.findById(new LabMemberKey(newRecord.getLabId(), newRecord.getUserId()))
                .orElseThrow(() -> new AppException(ErrorCode.NO_RELATION));
        if (request.getLogType() == LogType.ILLEGAL) {
            if (newRecord.getRecordType() == RecordType.CHECKIN && file == null) throw new AppException(ErrorCode.LOG_CREATE_ERROR);
            newRecord.setStatus(LogStatus.ILLEGAL);
            member.setMemberStatus(MemberStatus.BLOCKED);
            labMemberRepository.save(member);
            //Notify Task
            notifyIllegalAccess(request.getLabId());
        }
        // If legal, check duplicate
        else {
            if (newRecord.getRecordType() == RecordType.CHECKIN) {
                if (recentLog!= null && recentLog.getRecordType() == RecordType.CHECKIN) throw new AppException(ErrorCode.DUPLICATE_CHECK_IN);
            }
            if (newRecord.getRecordType() == RecordType.CHECKOUT && recentLog!= null && recentLog.getRecordType() == RecordType.CHECKOUT) {
                throw new AppException(ErrorCode.DUPLICATE_CHECK_OUT);
            }
            newRecord.setStatus(LogStatus.SUCCESS);
        }
        newRecord = logRepository.save(newRecord);
        try {
            if (file != null) {
                newRecord.setPhotoURL(
                        cloudinaryService.uploadFileLog(
                                newRecord.getId(), file, "Normal").getUrl()
                );
            }
        }
        catch (AppException e) {
            throw new AppException(e.getErrorCode());
        }
        return logMapper.toLogRespond(logRepository.save(newRecord));
    }


    // Get list of log (include pageSize and pageNumber)
    public List<LogRespond> getLogs(String labId) {
        globalUtils.checkAuthorizeManager(labId);
        List<LaboratoryLog> logs = logRepository.findByLabId(labId);
        List<LogRespond> logList = new ArrayList<>();

        LogRespond logElement = new LogRespond();
        MyUser user = new MyUser();
        for (LaboratoryLog log : logs) {
            logElement = logMapper.toLogRespond(log);
            user = myUserRepository.findById(log.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            logElement.setUserFirstName(user.getFirstName());
            logElement.setUserLastName(user.getLastName());
            logList.add(logElement);
        }
        logList.sort(Comparator.comparing(LogRespond::getRecordTime).reversed());
        return logList;
    }

    // Get log detail
    public LogDetailRespond getLogDetail(String logId) {
        LaboratoryLog log = logRepository.findById(logId).orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_EXIST));
        globalUtils.checkAuthorizeManager(log.getLabId());
        MyUser user = myUserRepository.findById(log.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        LogDetailRespond result = logMapper.toLogDetailRespond(log);
        result.setUserFirstName(user.getFirstName());
        result.setUserLastName(user.getLastName());

        return result;
    }

    // Delete logs with userId and labId
    public void deleteLog(String labId, String userId) throws IOException {
        List<LaboratoryLog> logs = logRepository.findByLabIdAndUserId(labId, userId);
        for (LaboratoryLog log : logs) {
            cloudinaryService.deleteFile(log.getId());
            logRepository.delete(log);
        }
    }

    // Weekly report
    public WeeklyReportResponse getWeeklyReport(String labId) {
        globalUtils.checkAuthorizeManager(labId);
        // Create result
        WeeklyReportResponse result = new WeeklyReportResponse();

        // Add totalUser (-1 for lab admin)
        result.setTotalUsers(labMemberRepository.findAllByLabMemberId_LabId(labId).size() - 1);
        result.setWeeklyAccess(0);

        // Create a list of weekday
        LocalDateTime now = LocalDateTime.now().plusHours(VNHour);
        LocalDateTime mondayMidnight = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<LocalDateTime> weekMidnights = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekMidnights.add(mondayMidnight.plusDays(i));
        }

        // Create return list
        List<DailyReportResponse> weeklyReport = new ArrayList<>();

        for (LocalDateTime day : weekMidnights) {
            DailyReportResponse dailyReport = new DailyReportResponse();

            // If date in the week is not yet then skip
            if (now.isBefore(day)) {
                dailyReport.setDayOfWeek(day.getDayOfWeek().toString());
                dailyReport.setCheckInCount(0);
                dailyReport.setCheckOutCount(0);
            }
            else {
                // Else then calculated
                List<LaboratoryLog> dailyLogs = null;
                if (day.getDayOfWeek() == now.getDayOfWeek()) {
                    dailyLogs = logRepository.findByLabIdAndRecordTimeBetween(labId, day, now);
                }
                else {
                    dailyLogs = logRepository.findByLabIdAndRecordTimeBetween(labId, day, day.plusDays(1));
                }
                if (dailyLogs != null) {
                    int totalRecord = dailyLogs.size();
                    // Add total day record into weekly access
                    result.setWeeklyAccess(result.getWeeklyAccess() + totalRecord);

                    int checkInRecord = (int) dailyLogs.stream()
                            .filter(log -> "CHECKIN".equals(log.getRecordType().toString()))
                            .count();

                    dailyReport.setDayOfWeek(day.getDayOfWeek().toString());
                    dailyReport.setCheckInCount(checkInRecord);
                    dailyReport.setCheckOutCount(totalRecord - checkInRecord);
                }
            }
            // Insert report into result
            weeklyReport.add(dailyReport);
        }

        // Add weekly report into result
        result.setWeeklyLogReport(weeklyReport);

        return result;
    }

    private void notifyIllegalAccess(String LabId) {
        //Notify task
        var managers = labMemberRepository.findAllByLabMemberId_LabIdAndRole(LabId, Role.MANAGER);
        for (LabMember manager: managers) {
            if (manager.getMyUser().getExpoPushToken() == null) continue;
            expoPushService.sendPushNotification(
                    manager.getMyUser().getExpoPushToken(),
                    "Unauthorized access",
                    "Someone has accessed it without permission, check the log for more details");
            notificationRepository.save(Notification.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(manager.getMyUser().getId())
                    .title("Unauthorized access")
                    .body("Someone has accessed it without permission, check the log for more details")
                    .type(NotifyType.TEXT)
                    .createdAt(new Date())
                    .build());
        }
    }
}
