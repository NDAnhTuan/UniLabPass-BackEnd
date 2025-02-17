package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.*;
import com.example.UniLabPass.dto.response.*;
import com.example.UniLabPass.service.AuthenticationService;
import com.example.UniLabPass.service.EmailService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    EmailService emailService;
    @Operation(summary = "Verify email with verification code", security = {@SecurityRequirement(name = "")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "404", description = "User not Existed", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    @PostMapping("/verify-email")
    CustomApiResponse<VerificationCodeResponse> verifyCode(@RequestBody VerificationCodeRequest request) {
        return CustomApiResponse.<VerificationCodeResponse>builder()
                .result(emailService.verifyCode(request))
                .build();

    }

    @Operation(summary = "Log in to the application", security = {@SecurityRequirement(name = "")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "USER_NOT_EXISTED 1005, UNVERIFIED_EMAIL 1008", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))

    })
    @PostMapping("/login")
    CustomApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return CustomApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();

    }

    @Operation(summary = "Check the validity of the token", security = {@SecurityRequirement(name = "")})
    @PostMapping("/introspect")
    CustomApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return CustomApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();

    }

    @Operation(summary = "Get refresh token", security = {@SecurityRequirement(name = "")})
    @PostMapping("/refresh")
    CustomApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return CustomApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @Operation(summary = "Logout", security = {@SecurityRequirement(name = "")})
    @PostMapping("/logout")
    CustomApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return CustomApiResponse.<Void>builder()
                .build();
    }
}
