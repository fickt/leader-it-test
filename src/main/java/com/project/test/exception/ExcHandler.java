package com.project.test.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestControllerAdvice
public class ExcHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AccountNotFoundException.class, ClientNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFoundException(Exception e) {
        ApiError error = new ApiError();
        error.setReason(e.getMessage());
        error.setResponseStatus(HttpStatus.NOT_FOUND.getReasonPhrase());
        error.setResponseCode(HttpStatus.NOT_FOUND.value());
        error.setTimeStamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NotAccountOwnerException.class, WrongSecretWordException.class})
    public ResponseEntity<ApiError> handleAccessDeniedException(Exception e) {
        ApiError error = new ApiError();
        error.setReason(e.getMessage());
        error.setResponseStatus(HttpStatus.FORBIDDEN.getReasonPhrase());
        error.setResponseCode(HttpStatus.FORBIDDEN.value());
        error.setTimeStamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({InadequateSumToWithdrawException.class,
            InadequateSumToTransferException.class,
            InvalidSumException.class,
            UnsupportedOperationException.class})
    public ResponseEntity<ApiError> handleBadRequestException(Exception e) {
        ApiError error = new ApiError();
        error.setReason(e.getMessage());
        error.setResponseStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
        error.setResponseCode(HttpStatus.BAD_REQUEST.value());
        error.setTimeStamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiError> handleInternalServerException(Exception e) {
        ApiError error = new ApiError();
        error.setReason(e.getMessage());
        error.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        error.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setTimeStamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
