package com.example.UniLabPass.repository;

import com.example.UniLabPass.entity.EventLog;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventLogRepository extends JpaRepository<EventLog, String> {
    @Transactional
    void deleteAllByEventId(String eventId);

    @Transactional
    void deleteAllByGuestId(String guestId);

    List<EventLog> findAllByEventId(String eventId);
}
