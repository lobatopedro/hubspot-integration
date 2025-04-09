package com.challenge.hubspot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OAuthTokenService {

    private static final String TOKEN_KEY = "hubspot:access_token";
    private static final String REFRESH_TOKEN_KEY = "hubspot:refresh_token";
    private final StringRedisTemplate redisTemplate;

    public OAuthTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveAccessToken(String accessToken, Duration expiration) {
        redisTemplate.opsForValue().set(TOKEN_KEY, accessToken, expiration);
        log.info("Access token salvo no Redis com validade de {} segundos", expiration.getSeconds());
    }

    public String getAccessToken() {
        return redisTemplate.opsForValue().get(TOKEN_KEY);
    }

    public Duration getTokenExpiration() {
        Long expirationInSeconds = redisTemplate.getExpire(TOKEN_KEY, TimeUnit.SECONDS);
        if (expirationInSeconds == null || expirationInSeconds <= 0) {
            return Duration.ZERO;
        }
        return Duration.ofSeconds(expirationInSeconds);
    }

    public void saveRefreshToken(String refreshToken) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_KEY, refreshToken);
        log.info("Refresh token salvo no Redis.");
    }

    public String getRefreshToken() {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY);
    }

    public void deleteAccessToken() {
        redisTemplate.delete(TOKEN_KEY);
    }
}


