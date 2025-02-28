package com.example.UniLabPass.controller;

import com.example.UniLabPass.compositekey.EventGuestKey;
import com.example.UniLabPass.dto.request.*;
import com.example.UniLabPass.dto.response.*;
import com.example.UniLabPass.entity.EventGuest;
import com.example.UniLabPass.service.LabEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EventController {
    // variables
    LabEventService labEventService;

    // Add new event
    @PostMapping
    @Operation(summary = "Add new event to lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully add new event to lab"),
    })
    CustomApiResponse<String> createEvent(@RequestBody LabEventCreationRequest request) {
        labEventService.createEvent(request);
        return CustomApiResponse.<String>builder()
                .result("Successfully created new event")
                .build();
    }

    // View all events of lab
    @GetMapping("/{labId}")
    @Operation(summary = "View all lab's events", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get all events"),
    })
    CustomApiResponse<List<LabEventRespond>> getEvents(@PathVariable String labId) {
        return CustomApiResponse.<List<LabEventRespond>>builder()
                .result(labEventService.getEvents(labId))
                .build();
    }

    // Check current event of lab
    @GetMapping("/current/{labId}")
    @Operation(summary = "Get lab's current event", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get event"),
    })
    CustomApiResponse<LabEventRespond> getCurrentEvent(@PathVariable String labId) {
        return CustomApiResponse.<LabEventRespond>builder()
                .result(labEventService.getCurrentEvent(labId))
                .build();
    }

    // Update event
    @PutMapping("/update")
    @Operation(summary = "Update event's info", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully update events"),
    })
    CustomApiResponse<LabEventRespond> updateEvent(@RequestBody LabEventUpdateRequest request) {
        return CustomApiResponse.<LabEventRespond>builder()
                .result(labEventService.updateEvent(request))
                .build();
    }

    // Delete event
    @DeleteMapping("delete/{eventId}")
    @Operation(summary = "Delete event", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete event successfully"),
    })
    CustomApiResponse<String> deleteEvent(@PathVariable String eventId) {
        labEventService.deleteEvent(eventId);
        return CustomApiResponse.<String>builder()
                .result("Delete event successfully")
                .build();
    }
}
