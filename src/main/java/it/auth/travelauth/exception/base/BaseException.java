package it.auth.travelauth.exception.base;

import it.auth.travelauth.domain.dto.response.ErrorResponseDto;
import lombok.Getter;

@Getter
public class BaseException extends Exception {

    private final ErrorResponseDto errorResponse;
    private final Integer errorCode;
    private final int httpStatus;

    // Costruttore principale completo
    public BaseException(String message, Integer code, String detail, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
        this.httpStatus = httpStatus;
        this.errorResponse = ErrorResponseDto.builder()
                .message(message)
                .code(code)
                .detail(detail)
                .build();
    }

    // Costruttore semplificato senza detail
    public BaseException(String message, Integer code, int httpStatus) {
        super(message);
        this.errorCode = code;
        this.httpStatus = httpStatus;
        this.errorResponse = ErrorResponseDto.builder()
                .message(message)
                .code(code)
                .detail(null)
                .build();
    }

    // Costruttore con causa e detail
    public BaseException(String message, Integer code, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
        this.httpStatus = httpStatus;
        this.errorResponse = ErrorResponseDto.builder()
                .message(message)
                .code(code)
                .detail(cause != null ? cause.getMessage() : null)
                .build();
    }

    // Costruttore con ErrorResponseDto precostruito
    public BaseException(ErrorResponseDto errorResponse, int httpStatus) {
        super(errorResponse.getMessage());
        this.errorCode = errorResponse.getCode();
        this.httpStatus = httpStatus;
        this.errorResponse = errorResponse;
    }

    // Costruttore legacy (per compatibilità con il codice esistente)
    public BaseException(Exception ex, ErrorResponseDto errorResponseDto) {
        super(errorResponseDto.getMessage(), ex);
        this.errorCode = errorResponseDto.getCode();
        this.httpStatus = 500; // Default Internal Server Error
        this.errorResponse = errorResponseDto;
    }

}
