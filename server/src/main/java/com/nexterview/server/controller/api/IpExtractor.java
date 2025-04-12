package com.nexterview.server.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class IpExtractor {

    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String IP_DELIMITER = ",";

    public String extract(HttpServletRequest request) {
        String forwarded = request.getHeader(HEADER_X_FORWARDED_FOR);

        if (forwarded != null && !forwarded.isEmpty()) {
            return extractFirstIp(forwarded);
        }

        return request.getRemoteAddr();
    }

    private String extractFirstIp(String headerValue) {
        return headerValue.split(IP_DELIMITER)[0].trim();
    }
}
