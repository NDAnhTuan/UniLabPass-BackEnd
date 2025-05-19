package com.example.UniLabPass.repository;

import com.example.UniLabPass.entity.EventLog;
import com.example.UniLabPass.entity.LaboratoryLog;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventLogRepository extends JpaRepository<EventLog, String> {
    @Transactional
    void deleteAllByEventId(String eventId);

    @Transactional
    void deleteAllByGuestId(String guestId);

    List<EventLog> findAllByEventId(String eventId);

    Optional<EventLog> findFirstByGuestIdAndEventIdOrderByRecordTimeDesc(String guestId, String eventId);

    @Query(value = """
        SELECT *
        FROM event_log l
        WHERE l.record_type = 0
          AND l.status = 0
          AND NOT EXISTS (
              SELECT 1
              FROM event_log l2
              WHERE l2.guest_id = l.guest_id
                AND l2.event_id = l.event_id
                AND l2.record_type = 1
                AND l2.record_time > l.record_time
          )
        """, nativeQuery = true)
    List<EventLog> findUncheckoutCheckins();

}
