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

    // Ghi đè toàn bộ quyền (load từ DB)
    public void saveUserPermissions(Long userId, Set<String> permissions) {
        String key = "user:" + userId + ":permissions";
        log.info("Saving {} permissions for userId: {}", permissions.size(), userId);

        try {
            redisTemplate.delete(key); // Xóa cũ nếu có

            if (permissions != null && !permissions.isEmpty()) {
                permissions.forEach(p -> redisTemplate.opsForSet().add(key, p));
                redisTemplate.expire(key, 24, TimeUnit.HOURS);
                log.info("Saved permissions successfully");
            }
        } catch (Exception e) {
            log.error("Error saving permissions to Redis", e);
        }
    }

    // Thêm một hoặc nhiều quyền
    public void addPermissions(Long userId, Set<String> newPermissions) {
        String key = "user:" + userId + ":permissions";
        log.info("Adding {} permissions for userId: {}", newPermissions.size(), userId);

        try {
            if (newPermissions != null && !newPermissions.isEmpty()) {
                newPermissions.forEach(p -> redisTemplate.opsForSet().add(key, p));
                redisTemplate.expire(key, 24, TimeUnit.HOURS);
                log.info("Successfully added permissions");
            }
        } catch (Exception e) {
            log.error("Error adding permissions", e);
        }
    }

    // Xóa một hoặc nhiều quyền
    public void removePermissions(Long userId, Set<String> permissionsToRemove) {
        String key = "user:" + userId + ":permissions";
        log.info("Removing {} permissions for userId: {}", permissionsToRemove.size(), userId);

        try {
            if (permissionsToRemove != null && !permissionsToRemove.isEmpty()) {
                redisTemplate.opsForSet().remove(key, permissionsToRemove.toArray());
                log.info("Successfully removed permissions");
            }
        } catch (Exception e) {
            log.error("Error removing permissions", e);
        }
    }

    // Lấy tất cả quyền từ Redis
    public Set<String> getUserPermissions(Long userId) {
        String key = "user:" + userId + ":permissions";
        try {
            Set<Object> data = redisTemplate.opsForSet().members(key);
            if (data == null || data.isEmpty()) return Collections.emptySet();

            return data.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Error getting permissions from Redis", e);
            return Collections.emptySet();
        }
    }

    // Kiểm tra một quyền cụ thể
    public boolean hasPermission(Long userId, String permission) {
        String key = "user:" + userId + ":permissions";
        try {
            Boolean isMember = redisTemplate.opsForSet().isMember(key, permission);
            return Boolean.TRUE.equals(isMember);
        } catch (Exception e) {
            log.error("Error checking permission", e);
            return false;
        }
    }

//    // Xóa cache quyền người dùng
//    public void deleteUserPermissionsCache(Long userId) {
//        String key = "user:" + userId + ":permissions";
//        redisTemplate.delete(key);
//        log.info("Deleted permissions cache for userId: {}", userId);
//    }
}
