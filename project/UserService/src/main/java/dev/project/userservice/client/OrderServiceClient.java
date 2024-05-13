package dev.project.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    // 주문 ID를 통해 주문 상세 정보를 가져옵니다.
    @GetMapping("/orders/{orderId}")
    String getOrderDetails(@PathVariable("orderId") String orderId);

}