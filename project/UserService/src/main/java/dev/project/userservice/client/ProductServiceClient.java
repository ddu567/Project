package dev.project.userservice.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    // 제품 ID를 통해 제품 상세 정보를 가져옵니다.
    @GetMapping("/products/{productId}")
    String getProductDetails(@PathVariable("productId") String productId);

}