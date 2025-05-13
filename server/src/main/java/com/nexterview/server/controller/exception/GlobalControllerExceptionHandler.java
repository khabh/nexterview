package com.nexterview.server.controller.exception;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.util.ContentCachingRequestWrapper;

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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException exception
    ) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.name(), "유저 인증에 실패했습니다."));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResultException(NoResourceFoundException exception) {
        log.warn("존재하지 않는 자원 요청: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.name(), "존재하지 않는 자원입니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception, HttpServletRequest request) {
        String requestInfo = getRequestInfo(request);
        log.error("예상치 못한 서버 오류 발생\nRequest Info: {}\n", requestInfo);
        Throwable rootCause = ExceptionUtils.getRootCause(exception);
        log.error("Unexpected error at {}: {}", rootCause.getClass().getSimpleName(), rootCause.getMessage());
        String message = "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.";

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), message));
    }

    private String getRequestInfo(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Method=").append(request.getMethod()).append(", ");
        sb.append("URI=").append(request.getRequestURI()).append(", ");
        sb.append("Query=").append(request.getQueryString()).append(", ");
        sb.append("RemoteAddr=").append(request.getRemoteAddr());

        Enumeration<String> headerNames = request.getHeaderNames();
        sb.append(", Headers=[");
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            sb.append(", ").append(headerName).append("=").append(request.getHeader(headerName));
        }
        sb.append("]");

        if (request instanceof ContentCachingRequestWrapper wrapper) {
            String body = getRequestBody(wrapper);
            sb.append(", Body=").append(body);
        }

        return sb.toString();
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        try {
            byte[] buf = request.getContentAsByteArray();
            if (buf.length == 0) {
                return "[EMPTY]";
            }

            request.getCharacterEncoding();
            String encoding = request.getCharacterEncoding();
            String body = new String(buf, encoding).trim();

            int maxLength = 1000;
            if (body.length() > maxLength) {
                return body.substring(0, maxLength) + "... [TRUNCATED]";
            }

            return body;
        } catch (UnsupportedEncodingException e) {
            log.warn("요청 본문 파싱 실패", e);
            return "[ERROR]";
        }
    }
}
