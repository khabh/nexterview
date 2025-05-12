package com.nexterview.server.service;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PromptAccessLimiter {

    private static final String PREFIX = "prompt:access:ip:";
    private static final String LOCK_PREFIX = "prompt:access:ip:lock:";
    private static final String MARK_PRESENT = "1";
    private static final Duration TEMP_LOCK_TTL = Duration.ofSeconds(30);
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    private final StringRedisTemplate redisTemplate;

    public PromptAccessLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void validateAccess(String clientIp) {
        String lockKey = LOCK_PREFIX + clientIp;

        Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, MARK_PRESENT, TEMP_LOCK_TTL);
        if (Boolean.FALSE.equals(lockAcquired)) {
            throw new NexterviewException(NexterviewErrorCode.REQUEST_TEMPORARILY_LOCKED);
        }

        String key = PREFIX + clientIp;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(lockKey);
            throw new NexterviewException(NexterviewErrorCode.GUEST_PROMPT_ACCESS_EXCEEDED);
        }
    }

    public void markAccessed(String clientIp) {
        String key = PREFIX + clientIp;

        LocalDateTime now = LocalDateTime.now(ZONE_ID);
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        Duration untilMidnight = Duration.between(now, midnight);

        redisTemplate.opsForValue().set(key, MARK_PRESENT, untilMidnight);
        redisTemplate.delete(LOCK_PREFIX + clientIp);
    }
}
