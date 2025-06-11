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
        try {
            String key = "user:" + userId + ":permissions";
            log.info("Lưu quyền vào Redis với key: {}", key);
            
            // Xóa key cũ nếu có
            redisTemplate.delete(key);
            
            // Lưu từng quyền một để tránh lỗi serialization
            if (!permissions.isEmpty()) {
                for (String permission : permissions) {
                    redisTemplate.opsForSet().add(key, permission);
                }
                // Đặt thời gian sống cho key (24 giờ)
                redisTemplate.expire(key, 24, TimeUnit.HOURS);
            }
            
            log.info("Đã lưu {} quyền cho userId {} vào Redis", permissions.size(), userId);
        } catch (Exception e) {
            log.error("Lỗi khi lưu quyền vào Redis: ", e);
            // Không throw exception, chỉ ghi log
        }
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
        try {
            String key = "user:" + userId + ":permissions";
            Boolean isMember = redisTemplate.opsForSet().isMember(key, permission);
            boolean hasPermission = Boolean.TRUE.equals(isMember);
            log.debug("UserId: {} có quyền {}: {}", userId, permission, hasPermission);
            return hasPermission;
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra quyền từ Redis: ", e);
            return false;
        }
    }
}
