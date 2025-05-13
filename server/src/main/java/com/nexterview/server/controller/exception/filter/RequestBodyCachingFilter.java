package com.nexterview.server.controller.exception.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

public class RequestBodyCachingFilter extends OncePerRequestFilter {

    private static final Set<String> METHODS_TO_CACHE = Set.of("POST", "PUT", "PATCH");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String method = request.getMethod();
        if (METHODS_TO_CACHE.contains(method)) {
            ContentCachingRequestWrapper wrapped = new ContentCachingRequestWrapper(request);
            filterChain.doFilter(wrapped, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
