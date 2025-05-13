package com.nexterview.server.security.exception;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            String requestInfo = getRequestInfo(request);
            log.error("처리되지 못한 서버 오류 발생\nRequest Info: {}\n", requestInfo);
            Throwable rootCause = ExceptionUtils.getRootCause(exception);
            log.error("Unexpected error at {}: {}", rootCause.getClass().getSimpleName(), rootCause.getMessage());

            String json = String.format("{\"code\":\"%s\",\"message\":\"%s\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    "알 수 없는 서버 오류가 발생했습니다.");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        }
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

        return sb.toString();
    }
}
