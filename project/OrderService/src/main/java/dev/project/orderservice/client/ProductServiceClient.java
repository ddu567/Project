package dev.project.orderservice.client;

import dev.project.productservice.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(name = "productservice", url = "http://localhost:8082") // productService의 URL과 포트를 명시
public interface ProductServiceClient {

    @GetMapping("/products/{id}")
    Optional<Product> getProductById(@PathVariable("id") String productId);

    @PostMapping("/updateProduct")
    Product updateProduct(@RequestBody Product product, @RequestParam("id") Long id);

    @PutMapping("/products/{id}")
    Product updateProduct(@PathVariable("id") Long id, @RequestBody Product product);

}
