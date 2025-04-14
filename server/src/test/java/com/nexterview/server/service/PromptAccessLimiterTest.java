package com.nexterview.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class PromptAccessLimiterTest {

    @Autowired
    PromptAccessLimiter limiter;

    @Autowired
    StringRedisTemplate redisTemplate;

    private final String ip = "127.0.0.1";
    private final String lockKey = "prompt:access:ip:lock:" + ip;
    private final String accessKey = "prompt:access:ip:" + ip;

    @BeforeEach
    void cleanRedis() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void 잠금_획득에_실패하면_예외를_던진다() {
        redisTemplate.opsForValue().set(lockKey, "1", Duration.ofMinutes(5));

        assertThatThrownBy(() -> limiter.checkAccessOrThrow(ip))
                .isInstanceOf(NexterviewException.class)
                .hasMessage(NexterviewErrorCode.REQUEST_TEMPORARILY_LOCKED.getMessage());
    }

    @Test
    void 접근_기록이_이미_있으면_예외를_던지고_락을_해제한다() {
        redisTemplate.opsForValue().set(accessKey, "1", Duration.ofHours(1));

        assertThatThrownBy(() -> limiter.checkAccessOrThrow(ip))
                .isInstanceOf(NexterviewException.class)
                .hasMessage(NexterviewErrorCode.GUEST_PROMPT_ACCESS_EXCEEDED.getMessage());
    }

    @Test
    void 접근을_롤백한다() {
        redisTemplate.opsForValue().set(lockKey, "1", Duration.ofMinutes(5));

        limiter.rollbackAccess(ip);

        assertThat(redisTemplate.hasKey(lockKey)).isFalse();
    }

    @Test
    void 접근을_커밋한다() {
        redisTemplate.opsForValue().set(lockKey, "1", Duration.ofMinutes(5));

        limiter.commitAccess(ip);

        assertThat(redisTemplate.hasKey(lockKey)).isFalse();
        assertThat(redisTemplate.hasKey(accessKey)).isTrue();
    }
}
