package com.firstgoal.config;

import com.firstgoal.messaging.MessagePublisher;
import com.firstgoal.messaging.RedisMessagePublisher;
import com.firstgoal.service.CompetitionService;
import com.firstgoal.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisMessagingConfig {

    @Bean
    @Autowired
    MessageListenerAdapter messageListener(EventService eventService) {
        return new MessageListenerAdapter(eventService);
    }

    @Bean
    @Autowired
    RedisMessageListenerContainer redisContainer(
        LettuceConnectionFactory redisConnectionFactory,
        EventService eventService) {
        RedisMessageListenerContainer container
            = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListener(eventService), topic());
        return container;
    }

    @Bean
    @Autowired
    MessagePublisher redisPublisher(RedisTemplate<String, Object> redisTemplate) {
        return new RedisMessagePublisher(redisTemplate, topic());
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("competitionMessageQueue");
    }
}
