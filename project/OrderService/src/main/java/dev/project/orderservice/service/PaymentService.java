package dev.project.orderservice.service;

import dev.project.orderservice.client.ProductServiceClient;
import dev.project.orderservice.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final ProductServiceClient productServiceClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public PaymentService(ProductServiceClient productServiceClient, RedisTemplate<String, Object> redisTemplate) {
        this.productServiceClient = productServiceClient;
        this.redisTemplate = redisTemplate;
    }

    // 결제 페이지의 URL을 제공하는 메서드
    public String getPaymentUrl() {
        return "https://example.com/payment";
    }

    // 상품 ID와 수량을 받아 재고를 확인하고 감소시키는 메서드
    public boolean checkAndDecrementStock(String productId, int quantity) {
        String stockKey = "stock_" + productId;
        Long currentStock = redisTemplate.opsForValue().increment(stockKey, -quantity);

        if (currentStock != null && currentStock >= 0) {
            return true;
        } else {
            redisTemplate.opsForValue().increment(stockKey, quantity);
            logger.error("Stock decrement failed for product: " + productId);
            return false;
        }
    }

    // 결제 성공 시 처리하는 메서드
    public ResponseEntity<ApiResponse<String>> makePayment(Long productId, int quantity) {
        if (checkAndDecrementStock(productId.toString(), quantity)) {
            return ResponseEntity.ok(ApiResponse.success("결제가 성공적으로 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.failure("재고가 부족하여 결제를 진행할 수 없습니다."));
        }
    }
}