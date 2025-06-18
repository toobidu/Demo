package org.example.demo.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Lưu quyền người dùng vào Redis
    public void saveUserPermissions(Long userId, Set<String> permissions) {
        String key = "user:" + userId + ":permissions";
        log.info("Saving permissions for userId: {} into Redis", key);

        try {
            redisTemplate.delete(key); // Xóa cũ nếu có

            if (permissions != null && !permissions.isEmpty()) {
                permissions.forEach(p -> redisTemplate.opsForSet().add(key, p));
                redisTemplate.expire(key, 24, TimeUnit.HOURS);
                log.info("Saved {} permissions for userId: {}", permissions.size(), userId);
            }
        } catch (Exception e) {
            log.error("Lỗi khi lưu quyền vào Redis", e);
        }
    }

    // Lấy danh sách quyền từ Redis
    public Set<String> getUserPermissions(Long userId) {
        String key = "user:" + userId + ":permissions";
        log.debug("Retrieving permissions for userId: {}", userId);

        try {
            Set<Object> redisData = redisTemplate.opsForSet().members(key);
            if (redisData == null || redisData.isEmpty()) {
                log.warn("Cannot find permissions for userId: {}", userId);
                return Collections.emptySet();
            }

            Set<String> permissions = redisData.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());

            log.debug("Found {} permissions for userId: {}", permissions.size(), userId);
            return permissions;
        } catch (Exception e) {
            log.error("Error retrieving permissions from Redis", e);
            return Collections.emptySet();
        }
    }

    // Kiểm tra xem người dùng có một quyền cụ thể không
    public boolean hasPermission(Long userId, String permission) {
        String key = "user:" + userId + ":permissions";
        log.debug("Checking if userId: {} has permission: {}", userId, permission);

        try {
            Boolean isMember = redisTemplate.opsForSet().isMember(key, permission);
            boolean result = Boolean.TRUE.equals(isMember);
            log.debug("Result for userId: {}, permission: {} is: {}", userId, permission, result);
            return result;
        } catch (Exception e) {
            log.error("Error checking permission", e);
            return false;
        }
    }

    // Xóa cache quyền người dùng
    public void deleteUserPermissionsCache(Long userId) {
        String key = "user:" + userId + ":permissions";
        redisTemplate.delete(key);
        log.info("Deleted permissions cache for userId: {}", userId);
    }
}
