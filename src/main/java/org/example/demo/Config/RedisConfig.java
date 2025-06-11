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

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        try {
            log.info("Kết nối Redis tới {}:{}", redisHost, redisPort);
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
            return new LettuceConnectionFactory(config);
        } catch (Exception e) {
            log.error("Lỗi khi tạo Redis connection factory: ", e);
            throw e;
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        try {
            log.info("Cấu hình RedisTemplate");
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory());
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
            redisTemplate.afterPropertiesSet();
            return redisTemplate;
        } catch (Exception e) {
            log.error("Lỗi khi cấu hình RedisTemplate: ", e);
            throw e;
        }
    }
}
