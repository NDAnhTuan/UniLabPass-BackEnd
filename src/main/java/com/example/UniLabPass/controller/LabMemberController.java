package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.*;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.ErrorApiResponse;
import com.example.UniLabPass.dto.response.LabMemberResponse;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.service.LabMemberService;
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

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LabMemberController {
    LabMemberService labMemberService;

    // Create new relationship
    @Operation(summary = "Add new member to lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New member was added to this lab successfully"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to modify the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    @PostMapping("/add")
    CustomApiResponse<LabMemberResponse> addNewMember(@RequestBody @Valid LabMemberCreationRequest request) {
        CustomApiResponse customApiResponse = new CustomApiResponse<LabMemberResponse>();
        customApiResponse.setCode(1000);
        return CustomApiResponse.<LabMemberResponse>builder()
                .result(labMemberService.addLabMember(request))
                .build();
    }

    // Delete member from lab
    @Operation(summary = "Delete member from lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New member was added to this lab successfully"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to modify the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    @DeleteMapping("/delete")
    CustomApiResponse<String> deleteMember(@RequestBody @Valid LabMemberDeleteRequest request) {
        labMemberService.deleteLabMember(request);
        return CustomApiResponse.<String>builder()
                .result("Member deleted successfully")
                .build();
    }

    // Get member list
    @Operation(summary = "Get all member from lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New member was added to this lab successfully"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to modify the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    @GetMapping("/all/{labId}")
    CustomApiResponse<List<LabMember>> getAllMember(@PathVariable("labId") String labId) {
        CustomApiResponse customApiResponse = new CustomApiResponse<List<LabMember>>();
        customApiResponse.setCode(1000);
        return CustomApiResponse.<List<LabMember>>builder()
                .result(labMemberService.getAllLabMembers(labId))
                .build();
    }

    // Update member's status
    @Operation(summary = "Update member status of Lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New member was added to this lab successfully"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to modify the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    @PutMapping("/update")
    CustomApiResponse<LabMemberResponse> updateLabMemberStatus(@RequestBody @Valid LabMemberUpdateRequest request) {
        CustomApiResponse customApiResponse = new CustomApiResponse<LabMemberResponse>();
        customApiResponse.setCode(1000);
        return CustomApiResponse.<LabMemberResponse>builder()
                .result(labMemberService.updateLabMember(request))
                .build();
    }
}
