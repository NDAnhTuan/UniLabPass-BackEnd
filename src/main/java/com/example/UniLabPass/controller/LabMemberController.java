package com.example.UniLabPass.controller;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.InviteManagerForLabRequest;
import com.example.UniLabPass.dto.request.LabMemberCreationRequest;
import com.example.UniLabPass.dto.request.LabMemberUpdateRequest;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.ErrorApiResponse;
import com.example.UniLabPass.dto.response.LabMemberInfoRespond;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/lab-member")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LabMemberController {
    LabMemberService labMemberService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Add member into lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    CustomApiResponse<String> addLabMember(@RequestPart LabMemberCreationRequest request,
                                           @RequestPart MultipartFile file) throws Exception {
        labMemberService.addLabMember(request, file);
        return CustomApiResponse.<String>builder()
                .result("New member is successfully added to lab")
                .build();
    }

    @PostMapping("/invite")
    @Operation(summary = "invite manager into lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    CustomApiResponse<String> inviteManager(@RequestBody InviteManagerForLabRequest request) {
        return CustomApiResponse.<String>builder()
                .result(labMemberService.inviteManager(request))
                .build();
    }

    @PostMapping("/acceptInvite")
    @Operation(summary = "accept Invite manager into lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    CustomApiResponse<LabMemberResponse> acceptInvite(@RequestBody String labId) {
        return CustomApiResponse.<LabMemberResponse>builder()
                .result(labMemberService.acceptInvite(labId))
                .build();
    }

    @GetMapping("/labs/{labId}")
    @Operation(summary = "Get all members of lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    CustomApiResponse<List<LabMemberResponse>> getLabMembers(@PathVariable("labId") String labId) {
        return CustomApiResponse.<List<LabMemberResponse>>builder()
                .result(labMemberService.getLabMembers(labId))
                .build();
    }

    @GetMapping("/lab/{labId}/member/{memberId}")
    @Operation(summary = "Get member detailed info", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    CustomApiResponse<LabMemberInfoRespond> getLabMemberDetailInfo(@PathVariable("labId") String labId, @PathVariable("memberId") String memberId, @RequestParam("isQrCode") boolean isQrCode) throws Exception {
        return CustomApiResponse.<LabMemberInfoRespond>builder()
                .result(labMemberService.getLabMemberInfo(labId, memberId, isQrCode))
                .build();
    }

    // Delete member from lab
    @Operation(summary = "Delete member from lab", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member deleted successfully"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to modify the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    @DeleteMapping("labs/{labId}/users/{userId}")
    CustomApiResponse<String> deleteMember(@PathVariable String userId, @PathVariable String labId) throws IOException {
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
