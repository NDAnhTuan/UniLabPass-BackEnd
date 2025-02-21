package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.LabCreationRequest;
import com.example.UniLabPass.dto.request.LabDeleteRequest;
import com.example.UniLabPass.dto.request.LabUpdateRequest;
import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.ErrorApiResponse;
import com.example.UniLabPass.dto.response.LabResponse;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.service.LaboratoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequestMapping("/lab")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LaboratoryController {
    LaboratoryService laboratoryService;

    // Create new admin
    @Operation(summary = "Register new LabAdmin", security = {@SecurityRequirement(name = "")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The user was created successfully"),
            @ApiResponse(responseCode = "404", description = "INVALID_PASSWORD 1004, USERNAME_INVALID 1003, " +
                    "USER_EXISTED 1002, INVALID_DOB 1009", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    @PostMapping("/signup")
    CustomApiResponse<MyUserResponse> createLabAdmin(@RequestBody @Valid MyUserCreationRequest request) {
        CustomApiResponse customApiResponse = new CustomApiResponse<MyUserResponse>();
        customApiResponse.setCode(1000);
        return CustomApiResponse.<MyUserResponse>builder()
                .result(laboratoryService.createLabAdmin(request))
                .build();
    }

    // Create new laboratory
    @Operation(summary = "Create new Laboratory", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The lab was created successfully"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    @PostMapping("/create")
    CustomApiResponse<LabResponse> createLab(@RequestBody @Valid LabCreationRequest request) {
        CustomApiResponse customApiResponse = new CustomApiResponse<LabResponse>();
        customApiResponse.setCode(1000);
        return CustomApiResponse.<LabResponse>builder()
                .result(laboratoryService.createLaboratory(request))
                .build();
    }

    // Update Laboratory
    @Operation(summary = "Update existed Laboratory", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Laboratory info updated"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    @PutMapping("/{labId}")
    CustomApiResponse<LabResponse> updateLab(@PathVariable("labId") String labId, @RequestBody @Valid LabUpdateRequest request) {
        CustomApiResponse customApiResponse = new CustomApiResponse<LabResponse>();
        customApiResponse.setCode(1000);
        return CustomApiResponse.<LabResponse>builder()
                .result(laboratoryService.updateLaboratory(labId, request))
                .build();
    }

    // Delete Laboratory
    @Operation(summary = "Delete Laboratory", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Laboratory deleted successfully"),
            @ApiResponse(responseCode = "401", description = "You are not authorized", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    @DeleteMapping("/delete/{labId}")
    CustomApiResponse<String> deleteLab(@PathVariable("labId") String labid) {
        laboratoryService.deleteLaboratory(labid);
        return CustomApiResponse.<String>builder()
                .result("Lab deleted successfully")
                .build();
    }

    // View all labs
    @Operation(summary = "Get All Laboratories", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Laboratory deleted successfully"),
            @ApiResponse(responseCode = "401", description = "You are not authorized", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    @GetMapping("/all")
    CustomApiResponse<List<LabMember>> getAllLabs() {
        CustomApiResponse customApiResponse = new CustomApiResponse<LabResponse>();
        customApiResponse.setCode(1000);
        return CustomApiResponse.<List<LabMember>>builder()
                .result(laboratoryService.getAllLabs())
                .build();
    }
}
