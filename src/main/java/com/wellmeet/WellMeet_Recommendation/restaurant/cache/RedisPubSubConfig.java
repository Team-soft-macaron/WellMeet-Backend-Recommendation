package com.wellmeet.WellMeet_Recommendation.restaurant.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RedisPubSubConfig {

    @Bean
    public RedisMessageListenerContainer redisContainer(
        RedisConnectionFactory connectionFactory,
        RestaurantUpdateSubscriber restaurantUpdateSubscriber,
        ChannelTopic restaurantUpdateTopic
    ){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(restaurantUpdateSubscriber, restaurantUpdateTopic);
        return container;
    }

    @Bean
    public ChannelTopic restaurantUpdateTopic() {
        return new ChannelTopic("restaurant-update");
    }
}
