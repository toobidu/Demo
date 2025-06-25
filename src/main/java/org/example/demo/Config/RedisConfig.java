package org.example.demo.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        try {
            log.info("Configuring Redis connection to {}:{}", redisHost, redisPort);
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
            config.setPassword(redisPassword);

            LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
            factory.afterPropertiesSet();
            log.info("Redis connection factory created successfully");
            return factory;
        } catch (Exception e) {
            log.error("Failed to create Redis connection factory: ", e);
            throw e;
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        try {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(redisConnectionFactory());

            // Configure serializers
            template.setKeySerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
            template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

            template.afterPropertiesSet();
            log.info("Redis template configured successfully");
            return template;
        } catch (Exception e) {
            log.error("Failed to configure Redis template: ", e);
            throw e;
        }
    }
}
