package com.example.UniLabPass.repository;

import com.example.UniLabPass.compositekey.EventGuestKey;
import com.example.UniLabPass.entity.EventGuest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventGuestRepository extends JpaRepository<EventGuest, EventGuestKey> {
    void deleteAllByEventGuestKey_EventId(String eventGuestKeyEventId);

    List<EventGuest> findAllByEventGuestKey_EventId(String eventGuestKeyEventId);

    EventGuest findAllByEventGuestKey_GuestId(String eventGuestKeyGuestId);

    Optional<EventGuest> findByEventGuestKey_GuestId(String guestId);
}
