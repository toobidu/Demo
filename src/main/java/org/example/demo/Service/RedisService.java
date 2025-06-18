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
        log.info("Lưu quyền vào Redis với key: {}", key);

        try {
            redisTemplate.delete(key); // Xóa cũ nếu có

            if (permissions != null && !permissions.isEmpty()) {
                permissions.forEach(p -> redisTemplate.opsForSet().add(key, p));
                redisTemplate.expire(key, 24, TimeUnit.HOURS);
                log.info("Đã lưu {} quyền cho userId {} vào Redis", permissions.size(), userId);
            }
        } catch (Exception e) {
            log.error("Lỗi khi lưu quyền vào Redis", e);
        }
    }

    // Lấy danh sách quyền từ Redis
    public Set<String> getUserPermissions(Long userId) {
        String key = "user:" + userId + ":permissions";
        log.debug("Truy vấn quyền cho người dùng: {} từ Redis", userId);

        try {
            Set<Object> redisData = redisTemplate.opsForSet().members(key);
            if (redisData == null || redisData.isEmpty()) {
                log.warn("Không tìm thấy quyền nào cho user: {}", userId);
                return Collections.emptySet();
            }

            Set<String> permissions = redisData.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());

            log.debug("Lấy được {} quyền từ Redis cho user {}", permissions.size(), userId);
            return permissions;
        } catch (Exception e) {
            log.error("Lỗi khi lấy quyền từ Redis", e);
            return Collections.emptySet();
        }
    }

    // Kiểm tra xem người dùng có một quyền cụ thể không
    public boolean hasPermission(Long userId, String permission) {
        String key = "user:" + userId + ":permissions";
        log.debug("Kiểm tra quyền: userId={}, permission={}", userId, permission);

        try {
            Boolean isMember = redisTemplate.opsForSet().isMember(key, permission);
            boolean result = Boolean.TRUE.equals(isMember);
            log.debug("Người dùng ID: {} có quyền {}: {}", userId, permission, result);
            return result;
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra quyền từ Redis", e);
            return false;
        }
    }

    // Xóa cache quyền người dùng
    public void deleteUserPermissionsCache(Long userId) {
        String key = "user:" + userId + ":permissions";
        redisTemplate.delete(key);
        log.info("Xóa cache quyền cho userId: {}", userId);
    }
}
