package dev.project.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/orders/{orderId}")
    String getOrderDetails(@PathVariable("orderId") String orderId);
}