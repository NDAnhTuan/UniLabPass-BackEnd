package com.example.UniLabPass.mapper;

import com.example.UniLabPass.compositekey.EventGuestKey;
import com.example.UniLabPass.dto.request.EventGuestUpdateRequest;
import com.example.UniLabPass.dto.response.EventGuestRespond;
import com.example.UniLabPass.entity.EventGuest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EventGuestMapper {
    default EventGuestRespond toGuestRespond(EventGuest guest) {
        return EventGuestRespond.builder()
                .guestId(guest.getEventGuestKey().getGuestId())
                .guestName(guest.getName())
                .guestEmail(guest.getEmail())
                .build();
    }

    void updateEventGuest(@MappingTarget EventGuest eventGuest, EventGuestUpdateRequest request);
}
