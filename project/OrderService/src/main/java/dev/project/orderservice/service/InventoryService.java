package dev.project.orderservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    // 재고 관리
    public boolean manageStock(Long productId, int changeInQuantity) {
        String key = "stock:" + productId;
        Long stockLeft = redisTemplate.opsForValue().increment(key, changeInQuantity);
        if (stockLeft != null && stockLeft >= 0) {
            return true;
        } else {
            // 재고가 부족한 경우, 증가시켜 원상복구
            redisTemplate.opsForValue().increment(key, -changeInQuantity);
            return false;
        }
    }

}
