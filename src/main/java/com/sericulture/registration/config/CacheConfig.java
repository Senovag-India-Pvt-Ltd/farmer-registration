package com.sericulture.registration.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("otpCache") {
            @Override
            protected org.springframework.cache.concurrent.ConcurrentMapCache createConcurrentMapCache(String name) {
                return new org.springframework.cache.concurrent.ConcurrentMapCache(
                        name,
                        CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build().asMap(),
                        false);
            }
        };
    }
}
