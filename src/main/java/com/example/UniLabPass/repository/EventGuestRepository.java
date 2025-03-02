package com.example.UniLabPass.repository;

import com.example.UniLabPass.compositekey.EventGuestKey;
import com.example.UniLabPass.entity.EventGuest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventGuestRepository extends JpaRepository<EventGuest, EventGuestKey> {
    @Transactional
    void deleteAllByEventGuestKey_EventId(String eventGuestKeyEventId);

    List<EventGuest> findAllByEventGuestKey_EventId(String eventGuestKeyEventId);

    Optional<EventGuest> findByEventGuestKey(EventGuestKey eventGuestKey);
}
