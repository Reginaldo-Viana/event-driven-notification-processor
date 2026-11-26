package com.example.notificationprocessor;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {
    private static final Duration RETENTION = Duration.ofDays(7);
    private final StringRedisTemplate redisTemplate;

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryStart(String eventId) {
        Boolean created = redisTemplate.opsForValue().setIfAbsent("notification:processed:" + eventId, "1", RETENTION);
        return Boolean.TRUE.equals(created);
    }

    public void release(String eventId) {
        redisTemplate.delete("notification:processed:" + eventId);
    }
}
