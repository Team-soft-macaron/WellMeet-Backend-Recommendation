package com.wellmeet.WellMeet_Recommendation.restaurant.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RestaurantUpdateSubscriber implements MessageListener {

    private final CacheManager cacheManager;

    public RestaurantUpdateSubscriber(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = new String(message.getBody()).trim();
        log.info("invalidate restaurant key={}", key);

        Cache cache = cacheManager.getCache("restaurant");
        if (cache != null) {
            cache.evictIfPresent(key);
        } else {
            log.warn("Cache restaurant not found.");
        }
    }
}
