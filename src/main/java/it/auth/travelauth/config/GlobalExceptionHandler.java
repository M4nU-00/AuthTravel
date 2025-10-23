package it.auth.travelauth.config;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import it.auth.travelauth.domain.dto.response.ErrorResponseDto;
import it.auth.travelauth.exception.base.BaseException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Gestione generale di BaseException
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDto> handleBaseException(BaseException ex) {
        log.error("BaseException occurred: {} - Status: {}", ex.getMessage(), ex.getHttpStatus(), ex);

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ex.getErrorResponse());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponseDto handleBadCredentialsException(BadCredentialsException ex) {
        log.error("BaseException occurred: {} - Status: {}", ex.getMessage(), ex);

        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .code(403)
                .detail(ex.getMessage())
                .message(ex.getLocalizedMessage())
                .build();

        return errorResponseDto;
    }
}
