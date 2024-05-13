package dev.project.productservice.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://product-service")
public interface OrderServiceClient {

    // 주문 상세 정보 조회 API를 호출하는 메서드
    @GetMapping("/orders/{orderId}")
    String getOrderDetails(@PathVariable("orderId") String orderId);

}
