package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.request.MyUserUpdateRequest;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.service.MyUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MyUserController {
    MyUserService myUserService;
    @Operation(summary = "Register a new user", security = {@SecurityRequirement(name = "")})
    @PostMapping
    CustomApiResponse<MyUserResponse> createMyUser(@RequestBody @Valid MyUserCreationRequest request) {
        CustomApiResponse customApiResponse = new CustomApiResponse<MyUserResponse>();
        customApiResponse.setCode(100);
        return CustomApiResponse.<MyUserResponse>builder()
                .result(myUserService.createMyUser(request))
                .build();
    }
    @GetMapping
    @Operation(summary = "Get List User", security = {@SecurityRequirement(name = "Bearer Authentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })

    CustomApiResponse<List<MyUserResponse>> getMyUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Email: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority ->
                log.info(grantedAuthority.getAuthority()));

        return CustomApiResponse.<List<MyUserResponse>>builder()
                .result(myUserService.getMyUsers())
                .build();
    }
    @GetMapping("/{userId}")
    CustomApiResponse<MyUserResponse> getMyUser(@PathVariable("userId") String userId) {
        return CustomApiResponse.<MyUserResponse>builder()
                .result(myUserService.getMyUser(userId))
                .build();
    }

    @GetMapping("/myInfo")
    CustomApiResponse<MyUserResponse> getMyInfo() {
        return CustomApiResponse.<MyUserResponse>builder()
                .result(myUserService.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userId}")
    CustomApiResponse<String> deleteMyUser(@PathVariable String userId) {
        myUserService.deleteMyUser(userId);
        return CustomApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }
    @PutMapping("/{userId}")
    CustomApiResponse<MyUserResponse> updateMyUser(@PathVariable String userId, @RequestBody MyUserUpdateRequest request) {
        return CustomApiResponse.<MyUserResponse>builder()
                .result(myUserService.updateMyUser(userId, request))
                .build();
    }
}
