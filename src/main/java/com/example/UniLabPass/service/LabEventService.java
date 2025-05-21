package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.EventGuestKey;
import com.example.UniLabPass.dto.request.*;
import com.example.UniLabPass.dto.response.EventGuestRespond;
import com.example.UniLabPass.dto.response.EventLogRespond;
import com.example.UniLabPass.dto.response.LabEventRespond;
import com.example.UniLabPass.dto.response.LogRespond;
import com.example.UniLabPass.entity.*;
import com.example.UniLabPass.enums.LogStatus;
import com.example.UniLabPass.enums.RecordType;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.mapper.EventGuestMapper;
import com.example.UniLabPass.mapper.EventLogMapper;
import com.example.UniLabPass.mapper.EventMapper;
import com.example.UniLabPass.repository.*;
import com.example.UniLabPass.utils.AESEncryptionUtil;
import com.example.UniLabPass.utils.GlobalUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LabEventService {
    // variables
    LabEventRepository labEventRepository;
    EventGuestRepository eventGuestRepository;
    EventLogRepository eventLogRepository;

    CloudinaryService cloudinaryService;
    EmailService emailService;
    QrCodeService qrCodeService;

    EventMapper eventMapper;
    EventGuestMapper eventGuestMapper;
    EventLogMapper eventLogMapper;

//    AESEncryptionUtil aesEncryptionUtil;

    @NonFinal
    @Value("${app.Global.VNHour}")
    int VNHour;

    GlobalUtils globalUtils;
    // Add new event
    public LabEventRespond createEvent(LabEventCreationRequest request) {
        globalUtils.checkAuthorizeManager(request.getLabId());

        LabEvent newEvent = eventMapper.toEvent(request);
        if (newEvent.getStartTime() == null
        || newEvent.getEndTime() == null
        || newEvent.getStartTime().isAfter(newEvent.getEndTime())) {
            throw new AppException(ErrorCode.EVENT_TIME_ERROR   );
        }
        return eventMapper.toEventRespond(labEventRepository.save(newEvent));
    }

    // View all events of lab
    public List<LabEventRespond> getEvents(String labId) {
        List<LabEvent> events = labEventRepository.findAllByLabId(labId);
        List<LabEventRespond> results = new ArrayList<>();
        for (LabEvent event : events) {
            results.add(eventMapper.toEventRespond(event));
        }
        return results;
    }

    // Check current event of lab
    public LabEventRespond getCurrentEvent(String labId) {
        globalUtils.checkAuthorizeManager(labId);

        LocalDateTime currentTime = LocalDateTime.now().plusHours(VNHour);
        List<LabEvent> events = labEventRepository.findAllByLabId(labId);
        for (LabEvent event : events) {
            if (event.getStartTime().isBefore(currentTime)
             && event.getEndTime().isAfter(currentTime)) {
                return eventMapper.toEventRespond(event);
            }
        }
        return new LabEventRespond();
    }

    // Update event
    public LabEventRespond updateEvent(LabEventUpdateRequest request) {
        globalUtils.checkAuthorizeManager(request.getLabId());
        checkEventExists(request.getEventId());

        LabEvent updatedEvent = eventMapper.toEventUpdated(request);
        return eventMapper.toEventRespond(labEventRepository.save(updatedEvent));
    }

    // Delete event
    public void deleteEvent(String eventId) throws IOException {
        checkEventExists(eventId);
        // Delete all event logs and guest
        var eventLog = eventLogRepository.findAllByEventId(eventId);
        for (EventLog event: eventLog) {
            cloudinaryService.deleteFile(event.getId());
        }
        eventLogRepository.deleteAllByEventId(eventId);
        eventGuestRepository.deleteAllByEventGuestKey_EventId(eventId);
        labEventRepository.deleteById(eventId);
    }

    // Add list event guest (include guest name and id)
    public String addListEventGuests(String eventId, List<EventGuestCreationRequest> eventGuests) throws Exception {
        checkEventExists(eventId);

        List<String> newGuestIds = eventGuests.stream().map(EventGuestCreationRequest::getGuestId).toList();
        List<String> guestIds = eventGuestRepository.findAllByEventGuestKey_EventId(eventId)
                                                    .stream()
                                                    .map(eventGuest -> eventGuest.getEventGuestKey().getGuestId())
                                                    .toList();

        if (newGuestIds.stream().distinct().count() != eventGuests.size()) {
            return "Duplicate guestID in request";
        }

        if (newGuestIds.stream().anyMatch(guestIds::contains)) {
            return "Duplicate guestID with exist guests";
        }

        for (EventGuestCreationRequest guest : eventGuests) {
            eventGuestRepository.save(EventGuest.builder()
                            .eventGuestKey(new EventGuestKey(eventId, guest.getGuestId()))
                            .name(guest.getName())
                            .build());
//            String encodedUserId = aesEncryptionUtil.encrypt(guest.getGuestId());
//            byte[] qr = qrCodeService.generateQRCode(encodedUserId, 250, 250);
//            emailService.sendQRCode(guest.getEmail(),qr);
        }
        return "All event guests added";
    }

    // Get event guest list
    public List<EventGuestRespond> getEventGuests(String eventId) {
        checkEventExists(eventId);

        List<EventGuest> guestList = eventGuestRepository.findAllByEventGuestKey_EventId(eventId);
        List<EventGuestRespond> result = new ArrayList<>();
        for (EventGuest guest : guestList) {
            result.add(eventGuestMapper.toGuestRespond(guest));
        }
        return result;
    }

    // Get single guest info
    public EventGuestRespond getGuestInfo(String eventId, String guestId) throws Exception {
        checkEventExists(eventId);
//        var guestId = aesEncryptionUtil.decrypt(qrCode);
        EventGuestKey key = new EventGuestKey(eventId, guestId);
        return eventGuestMapper.toGuestRespond(eventGuestRepository.findByEventGuestKey(key).orElseThrow(
                () -> new AppException(ErrorCode.GUEST_NOT_EXIST)
        ));
    }

    // Update event guest
    public EventGuestRespond updateEventGuest(EventGuestUpdateRequest request) {
        checkEventExists(request.getEventGuestKey().getEventId());
        EventGuest currentGuest = eventGuestRepository.findById(request.getEventGuestKey()).orElseThrow(() -> new AppException(ErrorCode.GUEST_NOT_EXIST));
        currentGuest.setName(request.getGuestName());
        return eventGuestMapper.toGuestRespond(eventGuestRepository.save(currentGuest));
    }

    // Delete event guest

    public void deleteEventGuest(EventGuestKey eventGuestKey) {
        checkEventExists(eventGuestKey.getEventId());
        // Delete event log
        eventLogRepository.deleteAllByGuestId(eventGuestKey.getGuestId());
        eventGuestRepository.deleteById(eventGuestKey);
    }

    // Delete all event guests
    public void deleteAllEventGuest(String eventId) {
        checkEventExists(eventId);
        // Delete all event logs, too
        eventLogRepository.deleteAllByEventId(eventId);
        eventGuestRepository.deleteAllByEventGuestKey_EventId(eventId);
    }

    // Add event log
    public EventLogRespond addEventLog(EventLogCreationRequest request, MultipartFile file) throws Exception {
        if (request.getEventId() == null
        || request.getGuestId() == null
        || request.getRecordType() == null) {
            throw new AppException(ErrorCode.LOG_CREATE_ERROR);
        }
        checkEventExists(request.getEventId());
//        String decodeGuestId = aesEncryptionUtil.decrypt(request.getGuestId());
//        request.setGuestId(decodeGuestId);
        LocalDateTime currentTime = LocalDateTime.now().plusHours(VNHour);
        EventLog newLog = eventLogMapper.toEventLog(request);
        newLog.setRecordTime(currentTime);
        newLog.setStatus(LogStatus.SUCCESS);

        EventLog recentLog = eventLogRepository
                .findFirstByGuestIdAndEventIdOrderByRecordTimeDesc(
                        newLog.getGuestId(), newLog.getEventId()).orElse(
                        null
                );

        if (newLog.getRecordType() == RecordType.CHECKIN) {
            if (recentLog != null && recentLog.getRecordType() == RecordType.CHECKIN) throw new AppException(ErrorCode.DUPLICATE_CHECK_IN);
            else if (file == null) throw new AppException(ErrorCode.LOG_CREATE_ERROR);
        }

        if (newLog.getRecordType() == RecordType.CHECKOUT && recentLog == null) {
            throw new AppException(ErrorCode.FIRST_LOG);
        }

        if (newLog.getRecordType() == RecordType.CHECKOUT && recentLog != null && recentLog.getRecordType() == RecordType.CHECKOUT) {
            throw new AppException(ErrorCode.DUPLICATE_CHECK_OUT);
        }

        newLog = eventLogRepository.save(newLog);

        try {
            if (file != null) {
                newLog.setPhotoURL(
                        cloudinaryService.uploadFileLog(
                                newLog.getId(), file, "Event").getUrl()
                );
            }
        }
        catch (AppException e) {
            throw new AppException(e.getErrorCode());
        }
        return eventLogMapper.toEventLogRespond(eventLogRepository.save(newLog));

    }

    // View event log
    public List<EventLogRespond> getEventLogs(String eventId) {
        checkEventExists(eventId);
        List<EventLog> eventLogs = eventLogRepository.findAllByEventId(eventId);
        List<EventLogRespond> result = new ArrayList<>();
        for (EventLog eventLog : eventLogs) {
            EventGuestKey key = new EventGuestKey(eventId, eventLog.getGuestId());
            EventGuest guest = eventGuestRepository.findByEventGuestKey(key).orElseThrow(
                    () ->  new AppException(ErrorCode.GUEST_NOT_EXIST)
            );
            EventLogRespond eventLogRespond = eventLogMapper.toEventLogRespond(eventLog);
            eventLogRespond.setGuestName(guest.getName());
            result.add(eventLogRespond);
        }
        result.sort(Comparator.comparing(EventLogRespond::getRecordTime).reversed());
        return result;
    }

    // View event log detail
    public EventLogRespond getEventLogDetail(String logId) {
        EventLog eventLog = eventLogRepository.findById(logId).orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_EXIST));
        EventGuestKey key = new EventGuestKey(eventLog.getEventId(), eventLog.getGuestId());
        EventGuest guest = eventGuestRepository.findByEventGuestKey(key).orElseThrow(
                () ->  new AppException(ErrorCode.GUEST_NOT_EXIST)
        );
        EventLogRespond eventLogRespond = eventLogMapper.toEventLogRespond(eventLog);
        eventLogRespond.setGuestName(guest.getName());
        return eventLogRespond;
    }


    // Check if event exist
    public void checkEventExists(String eventId) {
        if (!labEventRepository.existsById(eventId)) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        }
    }

}
