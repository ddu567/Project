package dev.project.orderservice.client;

import dev.project.orderservice.config.FeignConfig;
import dev.project.orderservice.dto.ProductInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "product-service", configuration = FeignConfig.class)
public interface ProductServiceClient {

    // 상품 ID를 기반으로 상품 정보 조회
    @GetMapping("/products/{id}")
    ProductInfoDTO getProductById(@PathVariable("id") Long productId);

    //상품 정보 업데이트
    @PostMapping("/products/{id}/update")
    void updateProduct(@PathVariable("id") Long id, @RequestBody ProductInfoDTO productInfo);

}