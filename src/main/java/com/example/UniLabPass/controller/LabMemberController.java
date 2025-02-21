package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.LabMemberCreationRequest;
import com.example.UniLabPass.dto.request.LabMemberUpdateRequest;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.ErrorApiResponse;
import com.example.UniLabPass.dto.response.LabMemberResponse;
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
@RequestMapping("/lab-member")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LabMemberController {
    LabMemberService labMemberService;

    @PostMapping
    @Operation(summary = "", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
//            @ApiResponse(responseCode = "400", description = "USER_NOT_EXISTED 1005", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    CustomApiResponse<LabMemberResponse> addLabMember(LabMemberCreationRequest request) {
        return CustomApiResponse.<LabMemberResponse>builder()
                .result(labMemberService.addLabMember(request))
                .build();
    }

    @GetMapping("/labs/{labId}")
    @Operation(summary = "", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
//            @ApiResponse(responseCode = "400", description = "USER_NOT_EXISTED 1005", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    CustomApiResponse<List<LabMemberResponse>> getLabMembers(@PathVariable("labId") String labId) {
        return CustomApiResponse.<List<LabMemberResponse>>builder()
                .result(labMemberService.getLabMembers(labId))
                .build();
    }

    // Delete member from lab
    @Operation(summary = "Delete member from lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New member was added to this lab successfully"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to modify the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    @DeleteMapping("labs/{labId}/users/{userId}")
    CustomApiResponse<String> deleteMember(@PathVariable String userId, @PathVariable String labId) {
        labMemberService.deleteLabMember(labId,userId);
        return CustomApiResponse.<String>builder()
                .result("Member deleted successfully")
                .build();
    }

    @Operation(summary = "Update member status of Lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New member was added to this lab successfully"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to modify the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    @PutMapping()
    CustomApiResponse<LabMemberResponse> updateLabMemberStatus(@RequestBody @Valid LabMemberUpdateRequest request) {
        return CustomApiResponse.<LabMemberResponse>builder()
                .result(labMemberService.updateLabMember(request))
                .build();
    }
}
