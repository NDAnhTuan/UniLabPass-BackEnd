package com.example.UniLabPass.controller;

import com.example.UniLabPass.dto.request.MyUserCreationRequest;
import com.example.UniLabPass.dto.request.MyUserUpdateRequest;
import com.example.UniLabPass.dto.response.CustomApiResponse;
import com.example.UniLabPass.dto.response.ErrorApiResponse;
import com.example.UniLabPass.dto.response.MyUserResponse;
import com.example.UniLabPass.service.MyUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The user was created successfully"),
            @ApiResponse(responseCode = "404", description = "INVALID_PASSWORD 1004, USERNAME_INVALID 1003, " +
                    "USER_EXISTED 1002, INVALID_DOB 1009", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    @PostMapping("/signup")
    CustomApiResponse<MyUserResponse> createMyUser(@RequestBody @Valid MyUserCreationRequest request) {
        CustomApiResponse customApiResponse = new CustomApiResponse<MyUserResponse>();
        customApiResponse.setCode(1000);
        return CustomApiResponse.<MyUserResponse>builder()
                .result(myUserService.createMyUser(request))
                .build();
    }
    @GetMapping
    @Operation(summary = "Get List Users", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
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
    @Operation(summary = "Get User", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "USER_NOT_EXISTED 1005", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    CustomApiResponse<MyUserResponse> getMyUser(@PathVariable("userId") String userId) {
        return CustomApiResponse.<MyUserResponse>builder()
                .result(myUserService.getMyUser(userId))
                .build();
    }

    @GetMapping("/myInfo")
    @Operation(summary = "Get My Info", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved your info"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "USER_NOT_EXISTED 1005", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    CustomApiResponse<MyUserResponse> getMyInfo() {
        return CustomApiResponse.<MyUserResponse>builder()
                .result(myUserService.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete User", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to modify the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing to the resource you are trying to modify is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
    })
    CustomApiResponse<String> deleteMyUser(@PathVariable String userId) {
        myUserService.deleteMyUser(userId);
        return CustomApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }
    @PutMapping("/{userId}")
    @Operation(summary = "Update User", security = {@SecurityRequirement(name = "BearerAuthentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated your info"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden" , content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "INVALID_PASSWORD 1004, USERNAME_INVALID 1003, " +
                    "INVALID_DOB 1009, USER_NOT_EXISTED 1005", content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    CustomApiResponse<MyUserResponse> updateMyUser(@PathVariable String userId, @RequestBody MyUserUpdateRequest request) {
        return CustomApiResponse.<MyUserResponse>builder()
                .result(myUserService.updateMyUser(userId, request))
                .build();
    }
}
