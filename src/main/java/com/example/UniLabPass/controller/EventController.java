package com.example.UniLabPass.controller;

import com.example.UniLabPass.compositekey.EventGuestKey;
import com.example.UniLabPass.dto.request.*;
import com.example.UniLabPass.dto.response.*;
import com.example.UniLabPass.entity.EventGuest;
import com.example.UniLabPass.service.LabEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.Multipart;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @Operation(summary = "Add new event to lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully add new event to lab"),
    })
    CustomApiResponse<LabEventRespond> createEvent(
            @RequestPart(value = "eventInfo", required = true)
            @Schema(example = "\"{\\\"labId\\\":\\\"dda99be8-15dd-420d-90f5-77834f40d911\\\",\\\"name\\\":\\\"Unilabs Event\\\",\\\"startTime\\\":\\\"2025-03-01T10:00:00\\\",\\\"endTime\\\":\\\"2025-03-01T11:00:00\\\"}\"")
            String eventInfoJson,
            @RequestPart(value = "guestList", required = false) MultipartFile file)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        LabEventCreationRequest request;
        try {
            request = objectMapper.readValue(eventInfoJson, LabEventCreationRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON format for eventInfo", e);
        }
        LabEventRespond event = labEventService.createEvent(request);
        String result = labEventService.addListEventGuests(event.getId(), toEventGuestList(file));
        return CustomApiResponse.<LabEventRespond>builder()
                .result(event)
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

    // Extract info fron CSV
    public List<EventGuestCreationRequest> toEventGuestList(MultipartFile file) {
        List<EventGuestCreationRequest> guests = new ArrayList<>();

        if (file != null && !file.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

                for (CSVRecord record : csvParser) {
                    EventGuestCreationRequest guest = new EventGuestCreationRequest(
                            record.get("guestId"),
                            record.get("name")
                    );
                    guests.add(guest);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return guests;
    }
}
