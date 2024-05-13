package dev.project.orderservice.controller;

import dev.project.orderservice.dto.PurchaseRequest;
import dev.project.orderservice.service.CommonService;
import dev.project.orderservice.service.PaymentService;
import dev.project.orderservice.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CommonService commonService;

     // 결제 페이지 진입을 위한 API. 결제 가능 시간 확인 후, 상품 재고 확인과 함께 결제 페이지 URL을 반환합니다.
     @PostMapping("/entry")
     public ResponseEntity<ApiResponse<String>> paymentEntry(@RequestBody PurchaseRequest request) {
         if (!isPurchaseEnabled()) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.failure("구매 활성화 시간이 아닙니다."));
         }
         if (paymentService.checkAndDecrementStock(String.valueOf(request.getProductId()), request.getQuantity())) {
             String paymentUrl = paymentService.getPaymentUrl();
             return ResponseEntity.ok(ApiResponse.success("결제 페이지로 리디렉트합니다.", paymentUrl));
         } else {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure("재고가 부족합니다."));
         }
     }



    // 결제 처리 API. 회원 ID와 상품 ID, 수량 정보를 사용하여 결제를 진행합니다.
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<String>> makePayment(@RequestParam Long memberId, @RequestBody PurchaseRequest request) {
        if (!isPurchaseEnabled()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.failure("구매 활성화 시간이 아닙니다."));
        }

        try {
            commonService.getValidMember(memberId);
            commonService.getValidProduct(request.getProductId(), request.getQuantity());
            return paymentService.makePayment(request.getProductId(), request.getQuantity());
        } catch (IllegalArgumentException e) {
            // 입력 데이터가 유효하지 않은 경우
            return ResponseEntity.badRequest().body(ApiResponse.failure("입력값 오류: " + e.getMessage()));
        }
    }


     // 특정 시간에만 구매가 활성화되는지 확인하는 메서드
    private boolean isPurchaseEnabled() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.isAfter(LocalTime.of(14, 0)); // 오후 2시 이후에 구매 활성화
    }
}
