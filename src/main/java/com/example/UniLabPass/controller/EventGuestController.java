package com.example.UniLabPass.controller;

import com.example.UniLabPass.compositekey.EventGuestKey;
import com.example.UniLabPass.dto.request.EventGuestCreationRequest;
import com.example.UniLabPass.dto.request.EventGuestUpdateRequest;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.EventGuestRespond;
import com.example.UniLabPass.service.LabEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EventGuestController {
    // Variables
    LabEventService labEventService;

    // Add list event guest (include guest name and id)
    @PostMapping("/{eventId}/guests")
    @Operation(summary = "Add guests into event", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All event guests added"),
    })
    CustomApiResponse<String> addListEventGuests(@PathVariable String eventId, @RequestBody List<EventGuestCreationRequest> guests) {
        return CustomApiResponse.<String>builder()
                .result(labEventService.addListEventGuests(eventId, guests))
                .build();
    }

    // Get event guest list
    @GetMapping("/{eventId}/guests")
    @Operation(summary = "Get all guests of event", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All guests retrieved"),
    })
    CustomApiResponse<List<EventGuestRespond>> getListEventGuests(@PathVariable String eventId) {
        return CustomApiResponse.<List<EventGuestRespond>>builder()
                .result(labEventService.getEventGuests(eventId))
                .build();
    }
    // Get event guest list
    @GetMapping("/{eventId}/guests/{guestId}")
    @Operation(summary = "Get all guests of event", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All guests retrieved"),
    })
    CustomApiResponse<EventGuestRespond> getListEventGuests(@PathVariable String eventId, @PathVariable String guestId) {
        EventGuestKey key = new EventGuestKey(eventId, guestId);
        return CustomApiResponse.<EventGuestRespond>builder()
                .result(labEventService.getGuestInfo(key))
                .build();
    }

    // Update event guest
    @PutMapping("/guests/update")
    @Operation(summary = "Update guests of event", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guest info updated successfully"),
    })
    CustomApiResponse<EventGuestRespond> updateEventGuest(@RequestBody EventGuestUpdateRequest request) {
        return CustomApiResponse.<EventGuestRespond>builder()
                .result(labEventService.updateEventGuest(request))
                .build();
    }

    // Delete event guest
    @DeleteMapping("/guests/delete")
    @Operation(summary = "Delete guests of event", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guest deleted successfully"),
    })
    CustomApiResponse<String> deleteEventGuest(@RequestBody EventGuestKey key) {
        labEventService.deleteEventGuest(key);
        return CustomApiResponse.<String>builder()
                .result("Guest deleted successfully")
                .build();
    }

    // Delete all event guests
    @DeleteMapping("/{eventId}/guests/delete/all")
    @Operation(summary = "Delete all guests of event", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guests deleted successfully"),
    })
    CustomApiResponse<String> deleteEventGuest(@PathVariable String eventId) {
        labEventService.deleteAllEventGuest(eventId);
        return CustomApiResponse.<String>builder()
                .result("All guests deleted successfully")
                .build();
    }
}
