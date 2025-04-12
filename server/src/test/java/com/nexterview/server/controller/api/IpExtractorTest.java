package com.nexterview.server.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

class IpExtractorTest {

    private final IpExtractor ipExtractor = new IpExtractor();

    @Test
    void X_Forwarded_For_헤더가_존재하면_첫번째_IP를_반환한다() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.0.1, 10.0.0.1");

        String ip = ipExtractor.extract(request);

        assertThat(ip).isEqualTo("192.168.0.1");
    }

    @Test
    void X_Forwarded_For_헤더가_없으면_RemoteAddr을_반환한다() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String ip = ipExtractor.extract(request);

        assertThat(ip).isEqualTo("127.0.0.1");
    }

    @Test
    void X_Forwarded_For_헤더가_빈문자열이면_RemoteAddr을_반환한다() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String ip = ipExtractor.extract(request);

        assertThat(ip).isEqualTo("127.0.0.1");
    }
}
