package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.request.MyUserUpdateRequest;
import com.example.UniLabPass.dto.response.ApiResponse;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.service.MyUserService;
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
    @PostMapping
    ApiResponse<MyUserResponse> createMyUser(@RequestBody @Valid MyUserCreationRequest request) {
        ApiResponse apiResponse = new ApiResponse<MyUserResponse>();
        apiResponse.setCode(100);
        return ApiResponse.<MyUserResponse>builder()
                .result(myUserService.createMyUser(request))
                .build();
    }
    @GetMapping
    ApiResponse<List<MyUserResponse>> getMyUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Email: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority ->
                log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<MyUserResponse>>builder()
                .result(myUserService.getMyUsers())
                .build();
    }
    @GetMapping("/{userId}")
    ApiResponse<MyUserResponse> getMyUser(@PathVariable("userId") String userId) {
        return ApiResponse.<MyUserResponse>builder()
                .result(myUserService.getMyUser(userId))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<MyUserResponse> getMyInfo() {
        return ApiResponse.<MyUserResponse>builder()
                .result(myUserService.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteMyUser(@PathVariable String userId) {
        myUserService.deleteMyUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }
    @PutMapping("/{userId}")
    ApiResponse<MyUserResponse> updateMyUser(@PathVariable String userId, @RequestBody MyUserUpdateRequest request) {
        return ApiResponse.<MyUserResponse>builder()
                .result(myUserService.updateMyUser(userId, request))
                .build();
    }
}
