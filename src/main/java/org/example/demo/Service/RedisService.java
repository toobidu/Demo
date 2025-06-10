package org.example.demo.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    //Lưu quyền cho người dùng vào Redis với TTL là 24 giờ
    public void saveUserPermissions(Long userId, Set<String> permissions) {
        log.debug("Lưu quyền cho người dùng: {}", userId);
        String key = "authorities:" + userId;
        redisTemplate.opsForValue().set(key, permissions, 24, TimeUnit.HOURS);
        log.info("Quyền đã được lưu vào Redis cho người dùng: {}", userId);
    }

    //Lấy danh sách quyền của người dùng từ Redis
    public Set<String> getUserPermissions(Long userId) {
        log.debug("Truy vấn quyền cho người dùng: {} từ Redis", userId);
        String key = "authorities:" + userId;
        Object permissions = redisTemplate.opsForValue().get(key);
        if (permissions instanceof Set) {
            return (Set<String>) permissions;
        }
        log.warn("Không có quyền nào từ người dùng: {} trong Redis", userId);
        return null;
    }

    //Kiểm tra quyền của người dùng
    //Nếu true -> có quyền
    //Nếu false -> không có quyền
    public boolean hasAuth2(Long userId, String permission) {
        Set<String> permissions = getUserPermissions(userId);
        if (permissions == null || permissions.isEmpty()) {
            log.warn("Không có quyền nào của userId: {} trong Redis.", userId);
        }
        boolean hasPermission = permissions.contains(permission);
        log.debug("UserId: {} có quyền: {}: {}", userId, permission, hasPermission);
        return hasPermission;
    }
}
