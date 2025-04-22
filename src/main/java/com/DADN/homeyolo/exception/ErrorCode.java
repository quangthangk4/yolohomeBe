package com.DADN.homeyolo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1009, "Invalid email address", HttpStatus.BAD_REQUEST),
    EMAIL_IS_REQUIRED(1009, "Email is required", HttpStatus.BAD_REQUEST),
    INCORRECT_PASSWORD(1010, "Incorrect password", HttpStatus.BAD_REQUEST),
    INVALID_VALUE_LIGHT(1011, "Invalid value for light. Only 0 (OFF) or 1 (ON) are accepted", HttpStatus.BAD_REQUEST),
    INVALID_VALUE_FAN_OR_MINLIGHT(1012, "Invalid value for fan or minLight. Only values 0-100 are accepted", HttpStatus.BAD_REQUEST),
    INVALID_FEED_KEY(1013, "Unknown feed key", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(1014, "Username existed, please choose another username", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1015, "Email existed, please choose another email", HttpStatus.BAD_REQUEST),
    ERROR_WHEN_CALL_ADAFRUIT_API(1016, "Error when calling Adafruit API", HttpStatus.INTERNAL_SERVER_ERROR),
    EMPTY_PASSWORD(1017, "Password is required", HttpStatus.BAD_REQUEST),
    DOOR_NOT_EXISTED(1018, "Door not existed", HttpStatus.NOT_FOUND),
    EMPTY_FIELD(1019, "Empty field", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
