package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.LogCreationRequest;
import com.example.UniLabPass.dto.response.*;
import com.example.UniLabPass.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LogController {
    LogService logService;

    // Add new logs
    @Operation(summary = "Add new log into lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Log was created successfully"),
    })
    @PostMapping()
    CustomApiResponse<String> createNewLog(@RequestBody @Valid LogCreationRequest request) {
        return CustomApiResponse.<String>builder()
                .result(logService.addNewLog(request))
                .build();
    }

    // Get all logs of lab
    @Operation(summary = "Get all logs of lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get logs successfully"),
    })
    @GetMapping("/{labId}")
    CustomApiResponse<List<LogRespond>> getAllLogs(@PathVariable String labId) {
        return CustomApiResponse.<List<LogRespond>>builder()
                .result(logService.getLogs(labId))
                .build();
    }

    // Get log detail
    @Operation(summary = "Get log details", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get logs successfully"),
    })
    @GetMapping("/detail/{logId}")
    CustomApiResponse<LogDetailRespond> getLogDetail(@PathVariable String logId) {
        return CustomApiResponse.<LogDetailRespond>builder()
                .result(logService.getLogDetail(logId))
                .build();
    }

    // Get weekly report
    @Operation(summary = "Get log's weekly report", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get logs successfully"),
    })
    @GetMapping("/{logId}/weekly")
    CustomApiResponse<List<DailyReportResponse>> getWeeklyReport(@PathVariable String logId) {
        return CustomApiResponse.<List<DailyReportResponse>>builder()
                .result(logService.getWeeklyReport(logId))
                .build();
    }
}
