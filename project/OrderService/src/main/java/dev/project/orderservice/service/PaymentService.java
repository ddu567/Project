package dev.project.orderservice.service;

import dev.project.orderservice.client.ProductServiceClient;
import dev.project.orderservice.dto.ApiResponse;
import dev.project.orderservice.dto.PaymentSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final ProductServiceClient productServiceClient;
    private final RedisTemplate<String, PaymentSession> redisTemplate;

    @Autowired
    public PaymentService(ProductServiceClient productServiceClient, RedisTemplate<String, PaymentSession> redisTemplate) {
        this.productServiceClient = productServiceClient;
        this.redisTemplate = redisTemplate;
    }

    // 결제 세션을 초기화하고 세션 ID를 생성합니다.
    public Long initiatePayment(Long productId, int quantity) {
        Long sessionId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE; // 고유 세션 ID 생성
        PaymentSession session = new PaymentSession(sessionId, productId, quantity, LocalDateTime.now(), false);
        redisTemplate.opsForValue().set("payment_session:" + sessionId, session, 15, TimeUnit.MINUTES);  // 세션에 15분 타임아웃 설정
        return sessionId;
    }

    // 세션 ID를 기반으로 결제를 완료합니다.
    public ResponseEntity<ApiResponse<String>> completePayment(Long sessionId) {
        PaymentSession session = redisTemplate.opsForValue().get("payment_session:" + sessionId);
        if (session == null || session.isCompleted()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure("Session expired or already completed."));
        }
        session.setCompleted(true);  // 세션 완료 상태로 설정
        redisTemplate.delete("payment_session:" + sessionId);  // 세션 데이터 삭제
        return makePayment(session.getProductId(), session.getQuantity());  // 실제 결제 처리 호출
    }

    // 결제 페이지 URL을 제공합니다.
    public String getPaymentUrl() {
        return "https://example.com/payment";
    }

    // 상품 ID와 수량을 받아 재고를 확인하고 감소시키는 메서드입니다.
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

    // 결제 성공 시 처리하는 메서드입니다.
    public ResponseEntity<ApiResponse<String>> makePayment(Long productId, int quantity) {
        if (checkAndDecrementStock(productId.toString(), quantity)) {
            return ResponseEntity.ok(ApiResponse.success("결제가 성공적으로 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.failure("재고가 부족하여 결제를 진행할 수 없습니다."));
        }
    }

    // 만료된 결제 세션을 확인하고 처리하는 스케줄러입니다.
    @Scheduled(fixedDelay = 60000)  // 매 1분마다 실행
    public void checkForExpiredSessions() {
        Set<String> keys = redisTemplate.keys("payment_session:*");
        for (String key : keys) {
            PaymentSession session = redisTemplate.opsForValue().get(key);
            if (session != null && !session.isCompleted() && Duration.between(session.getStartTime(), LocalDateTime.now()).toMinutes() >= 15) {
                session.setCompleted(true);  // 세션 종료 처리
                redisTemplate.delete(key);  // 세션 삭제
                logger.info("Expired payment session {} removed.", session.getSessionId());
            }
        }
    }

}
