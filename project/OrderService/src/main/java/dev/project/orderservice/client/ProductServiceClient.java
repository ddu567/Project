package dev.project.orderservice.client;

import dev.project.orderservice.dto.ProductInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(name = "productservice", url = "http://localhost:8082")
public interface ProductServiceClient {

    @GetMapping("/products/{id}")
    Optional<ProductInfoDTO> getProductById(@PathVariable("id") Long productId);

    @PostMapping("/updateProduct")
    void updateProduct(@RequestBody ProductInfoDTO product);

}
