package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.EventLogCreationRequest;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.EventLogRespond;
import com.example.UniLabPass.service.LabEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EventLogController {
    // Variables
    LabEventService labEventService;

    // Add event log
    @PostMapping(value = "/logs", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Add new event log", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CHECKIN/CHECKOUT successfully in this event"),
    })
    CustomApiResponse<EventLogRespond> addEventLog(@RequestPart EventLogCreationRequest request,@RequestPart MultipartFile file) throws IOException {
        return CustomApiResponse.<EventLogRespond>builder()
                .result(labEventService.addEventLog(request,file))
                .build();
    }

    // View event log
    @GetMapping("/{eventId}/logs")
    @Operation(summary = "Get all logs of event", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get event logs successfully"),
    })
    CustomApiResponse<List<EventLogRespond>> getEventLogs(@PathVariable String eventId) {
        return CustomApiResponse.<List<EventLogRespond>>builder()
                .result(labEventService.getEventLogs(eventId))
                .build();
    }

    // View event log detail
    @GetMapping("/logs/{logId}")
    @Operation(summary = "Get log details of event", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get event log detail successfully"),
    })
    CustomApiResponse<EventLogRespond> getEventLogDetail(@PathVariable String logId) {
        return CustomApiResponse.<EventLogRespond>builder()
                .result(labEventService.getEventLogDetail(logId))
                .build();
    }
}
