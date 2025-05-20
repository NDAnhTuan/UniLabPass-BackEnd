package com.example.UniLabPass.job;
import com.example.UniLabPass.entity.EventLog;
import com.example.UniLabPass.entity.LaboratoryLog;
import com.example.UniLabPass.enums.LogStatus;
import com.example.UniLabPass.enums.RecordType;
import com.example.UniLabPass.repository.EventLogRepository;
import com.example.UniLabPass.repository.LogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MyCronJob {
    LogRepository logRepository;
    EventLogRepository eventLogRepository;
    @NonFinal
    @Value("${app.Global.VNHour}")
    int VNHour;
    // Chạy vào 2:30 sáng mỗi ngày
    @Scheduled(cron = "0 0 0 * * *")
    public void autoCheckout() throws IOException {
        System.out.println("Cron job running at 2:00 AM...");
        LocalDateTime now = LocalDateTime.now().plusHours(VNHour);
        // Logic xử lý ở đây
        List<LaboratoryLog> logs = logRepository.findUncheckoutCheckins();
        List<EventLog> eventLogs = eventLogRepository.findUncheckoutCheckins();

        for (LaboratoryLog log : logs) {
            LaboratoryLog request = LaboratoryLog.builder()
                    .labId(log.getLabId())
                    .userId(log.getUserId())
                    .recordType(RecordType.CHECKOUT)
                    .recordTime(now)
                    .status(LogStatus.AUTO)
                    .photoURL(log.getPhotoURL())
                    .build();
            logRepository.save(request);
        }

        for (EventLog eventLog : eventLogs) {
            EventLog request = EventLog.builder()
                    .eventId(eventLog.getEventId())
                    .guestId(eventLog.getGuestId())
                    .recordType(RecordType.CHECKOUT)
                    .recordTime(now)
                    .status(LogStatus.AUTO)
                    .photoURL(eventLog.getPhotoURL())
                    .build();
            eventLogRepository.save(request);
        }


    }
}

