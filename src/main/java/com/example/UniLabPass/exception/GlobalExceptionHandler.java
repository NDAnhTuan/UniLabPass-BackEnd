package com.example.UniLabPass.exception;

import com.example.UniLabPass.dto.response.CustomApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<CustomApiResponse> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        CustomApiResponse customApiResponse = new CustomApiResponse();

        customApiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        customApiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(customApiResponse);
    }
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<CustomApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        CustomApiResponse customApiResponse = new CustomApiResponse();
        customApiResponse.setCode(errorCode.getCode());
        customApiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(customApiResponse);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<CustomApiResponse> handlingAuthorizationDeniedException(AuthorizationDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        CustomApiResponse customApiResponse = new CustomApiResponse();
        customApiResponse.setCode(errorCode.getCode());
        customApiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(customApiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<CustomApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        //attributes là thuộc tính annotation
        Map<String,Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
            // exception.getBindingResult() : thông tin lỗi gồm field, type, mess dạng wrap
            var constraintViolation = exception.getBindingResult()
                    .getAllErrors().get(0).unwrap(ConstraintViolation.class);
            // getConstraintDescriptor() Nội dung annotation
            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            log.info(attributes.toString());
        } catch (IllegalArgumentException e) {}

        CustomApiResponse customApiResponse = new CustomApiResponse();

        customApiResponse.setCode(errorCode.getCode());
        customApiResponse.setMessage(Objects.nonNull(attributes) ?
                mapAttribute(errorCode.getMessage(), attributes) :
                errorCode.getMessage()
        );
        return ResponseEntity.badRequest().body(customApiResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        // Thay {min} thành giá trị minValue
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);

    }

}
