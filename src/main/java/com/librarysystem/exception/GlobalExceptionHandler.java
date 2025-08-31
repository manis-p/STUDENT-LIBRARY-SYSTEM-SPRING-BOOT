package com.librarysystem.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.librarysystem.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", request);
    }

    @ExceptionHandler(EmailSendFailedException.class)
    public ResponseEntity<ErrorResponse> handleEmailSendFail(EmailSendFailedException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_FAILED", request);
    }

    @ExceptionHandler(nvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handletoTomenIvalid(nvalidTokenException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Ivalid_tokn", request);
    }

    // verify code Exception
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOtp(InvalidOtpException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, "INVALID_OTP", request);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpired(TokenExpiredException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", request);
    }

    // via OldPassword change Password
    @ExceptionHandler(IncorrectOldPasswordException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpired(IncorrectOldPasswordException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.UNAUTHORIZED, "Incorrect_Old_Password", request);
    }

    // singe ogout ke time ka error for sin sinde device
    @ExceptionHandler(handleInvalidRequest.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(handleInvalidRequest ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, "INVALID_REQUEST", request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, "USER_NOT_FOUND", request);
    }

    // ye signu ke time ka hai
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, "Email_Already_Exists_Exception", request);
    }

    // when user try to ogim with deted data .
    @ExceptionHandler(UserDeletedException.class)
    public ResponseEntity<ErrorResponse> handleUserDeleted(UserDeletedException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, "User_Deleted_Exception", request);
    }
    // verify ke time

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ErrorResponse> handleOtpExpired(OtpExpiredException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.UNAUTHORIZED, "Otp_Expired_Exception", request);
    }

    // data base ke time
    @ExceptionHandler(InvalidUserDetailsTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserType(InvalidUserDetailsTypeException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.UNAUTHORIZED, "Otp_Expired_Exception", request);
    }

    /// token ke time
    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ErrorResponse> handleTokenError(TokenGenerationException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.UNAUTHORIZED, "Token_Generation_Exception", request);
    }

    // data base ke time
    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ErrorResponse> handleDbOperationError(DatabaseOperationException ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.UNAUTHORIZED, "Token_Generation_Exception", request);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + ex.getMessage());
    }
    // ye used hai vaidation ke error ko handr karne ke iye.

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> buildResponse(Exception ex, HttpStatus status, String code,
            WebRequest request) {
        ErrorResponse error = new ErrorResponse(status.value(), ex.getMessage(), code,
                request.getDescription(false).replace("uri=", ""), LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

}
