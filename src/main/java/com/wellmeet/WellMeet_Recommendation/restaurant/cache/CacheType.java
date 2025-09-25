package com.wellmeet.WellMeet_Recommendation.restaurant.cache;

import lombok.Getter;

@Getter
public enum CacheType {
    RESTAURANT("restaurant", 10, 10000),
    LOCATION("location", 10, 10000);

    CacheType(String cacheName, int expiredAfterWrite, int maximumSize){
        this.cacheName = cacheName;
        this.expiredAfterWrite = expiredAfterWrite;
        this.maximumSize = maximumSize;        
    }

    private String cacheName;
    private int expiredAfterWrite;
    private int maximumSize;
}
