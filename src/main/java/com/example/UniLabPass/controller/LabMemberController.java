package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.LabMemberCreationRequest;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
