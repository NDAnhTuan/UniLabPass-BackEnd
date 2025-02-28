package com.example.UniLabPass.repository;

import com.example.UniLabPass.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventLogRepository extends JpaRepository<EventLog, String> {
    void deleteAllByEventId(String eventId);

    void deleteAllByGuestId(String guestId);

    List<EventLog> findAllByEventId(String eventId);
}
