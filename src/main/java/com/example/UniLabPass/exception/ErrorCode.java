package com.example.UniLabPass.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007,"You do not have permission", HttpStatus.FORBIDDEN),
    UNVERIFIED_EMAIL(1008, "Email is not verified\n", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1009, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    LAB_NAME_INVALID(1010, "Please enter the name of laboratory", HttpStatus.BAD_REQUEST),
    LAB_NOT_EXISTED(1011, "Laboratory not exist", HttpStatus.NOT_FOUND),
    NO_RELATION(1011, "This user is not existed on this lab", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED(1012, "Role not existed", HttpStatus.NOT_FOUND),
    MEMBER_NOT_EXISTED(1013, "Member not existed", HttpStatus.NOT_FOUND),
    MEMBER_ALREADY_EXISTED(1014, "Member is already exist in this lab", HttpStatus.CONFLICT),
    LOG_NOT_EXIST(1015, "This log is not exist", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(1016, "There is already a user with this Email", HttpStatus.CONFLICT),
    USER_ID_EXISTED(1017, "There is already a user with this ID", HttpStatus.CONFLICT),
    FALSE_USER_DATA(1018, "There is already a user with different info", HttpStatus.CONFLICT),
    EVENT_NOT_EXIST(1019, "There is no event with this ID", HttpStatus.NOT_FOUND),
    GUEST_NOT_EXIST(1020, "No guest found", HttpStatus.NOT_FOUND),
    EVENT_TIME_ERROR(1021, "Event start time and end time is not valid", HttpStatus.BAD_REQUEST),
    LOG_CREATE_ERROR(1022, "Log info missing or error", HttpStatus.BAD_REQUEST),;
    int code;
    String message;
    HttpStatus statusCode;
}
