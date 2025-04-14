package com.nexterview.server.controller.exception;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(NexterviewException.class)
    public ResponseEntity<ErrorResponse> handleNexterviewException(NexterviewException exception) {
        NexterviewErrorCode errorCode = exception.getErrorCode();
        HttpStatus status = ErrorCodeHttpStatus.getHttpStatus(errorCode);

        return new ResponseEntity<>(new ErrorResponse(errorCode.name(), exception.getMessage()), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        NexterviewErrorCode errorCode = NexterviewErrorCode.ARGUMENT_INVALID;
        String errorMessage = errorCode.getMessage() + exception.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" "));

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(errorCode.name(), errorMessage));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            InternalAuthenticationServiceException exception
    ) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.name(), "유저 인증에 실패했습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        log.error(exception.getMessage(), exception);
        String message = "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.";

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), message));
    }
}
