package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LogCreationRequest;
import com.example.UniLabPass.dto.response.DailyReportResponse;
import com.example.UniLabPass.dto.response.LogDetailRespond;
import com.example.UniLabPass.dto.response.LogRespond;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.LaboratoryLog;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.enums.LogStatus;
import com.example.UniLabPass.enums.MemberStatus;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.LogMapper;
import com.example.UniLabPass.repository.LabMemberRepository;
import com.example.UniLabPass.repository.LogRepository;
import com.example.UniLabPass.repository.MyUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LogService {
    MyUserRepository myUserRepository;
    LabMemberRepository labMemberRepository;
    LogRepository logRepository;

    LogMapper logMapper;

    // Add a new log
    public String addNewLog(LogCreationRequest request) {
        String message = request.getRecordType().toString();

        LaboratoryLog newRecord = logMapper.toLaboratoryLog(request);
        newRecord.setRecordTime(LocalDateTime.now());
        // Check if user has been blocked or not
        LabMember member = labMemberRepository.findById(new LabMemberKey(request.getLabId(), request.getUserId()))
                .orElseThrow(() -> new AppException(ErrorCode.NO_RELATION));
        if (member.getMemberStatus() == MemberStatus.BLOCKED) {
            newRecord.setStatus(LogStatus.BLOCKED);
            message += " denied since user has been blocked";
        }
        else {
            newRecord.setStatus(LogStatus.SUCCESS);
            message += " succeeded";
        }
        logRepository.save(newRecord);

        return message;
    }

    // Get list of log (include pageSize and pageNumber)
    public List<LogRespond> getLogs(String labId) {
        checkAuthorizeManager(labId);
        List<LaboratoryLog> logs = logRepository.findByLabId(labId);
        List<LogRespond> logList = new ArrayList<LogRespond>();

        LogRespond logElement = new LogRespond();
        MyUser user = new MyUser();
        for (LaboratoryLog log : logs) {
            logElement = logMapper.toLogRespond(log);
            user = myUserRepository.findById(log.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            logElement.setUserFirstName(user.getFirstName());
            logElement.setUserLastName(user.getLastName());
            logList.add(logElement);
        }
        return logList;
    }

    // Get log detail
    public LogDetailRespond getLogDetail(String logId) {
        LaboratoryLog log = logRepository.findById(logId).orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_EXIST));
        checkAuthorizeManager(log.getLabId());
        MyUser user = myUserRepository.findById(log.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        LogDetailRespond result = logMapper.toLogDetailRespond(log);
        result.setUserFirstName(user.getFirstName());
        result.setUserLastName(user.getLastName());

        return result;
    }

    // Delete logs with userId and labId
    public void deleteLog(String labId, String userId) {
        List<LaboratoryLog> logs = logRepository.findByLabIdAndUserId(labId, userId);
        for (LaboratoryLog log : logs) {
            logRepository.delete(log);
        }
    }

    // Weekly report
    public List<DailyReportResponse> getWeeklyReport(String labId) {
        checkAuthorizeManager(labId);
        // Create a list of weekday
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime mondayMidnight = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<LocalDateTime> weekMidnights = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekMidnights.add(mondayMidnight.plusDays(i));
        }

        // Create return list
        List<DailyReportResponse> result = new ArrayList<>();

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
                    dailyLogs = logRepository.findByRecordTimeBetween(day, now);
                }
                else {
                    dailyLogs = logRepository.findByRecordTimeBetween(day, day.plusDays(1));
                }
                if (dailyLogs != null) {
                    int totalRecord = dailyLogs.size();
                    int checkInRecord = (int) dailyLogs.stream()
                            .filter(log -> "CHECKIN".equals(log.getRecordType().toString()))
                            .count();

                    dailyReport.setDayOfWeek(day.getDayOfWeek().toString());
                    dailyReport.setCheckInCount(checkInRecord);
                    dailyReport.setCheckOutCount(totalRecord - checkInRecord);
                }
            }
            // Insert report into result
            result.add(dailyReport);
        }

        return result;
    }

    // Check authorize
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
