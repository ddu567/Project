package dev.project.productservice.controller;

import dev.project.productservice.dto.PurchaseRequest;
import dev.project.productservice.entity.Product;
import dev.project.productservice.repository.ProductRepository;
import dev.project.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    // 상품 목록 API
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    // 상품 상세 페이지 API
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // 상품 구매 API
    @PostMapping("/{id}/purchase")
    public ResponseEntity<String> purchaseProduct(@PathVariable Long id, @RequestBody PurchaseRequest purchaseRequest) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Product p = product.get();
        if (p.getStock() < purchaseRequest.getQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient stock.");
        }

        // 재고 감소 처리
        p.setStock(p.getStock() - purchaseRequest.getQuantity());
        productRepository.save(p);
        return ResponseEntity.ok("Product purchased successfully.");
    }

    // 상품 정보 수정 API
    @PostMapping("/update")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    // 남은 수량 API
    @GetMapping("/{id}/quantity")
    public ResponseEntity<Integer> getProductQuantity(@PathVariable Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            int quantity = product.getStock();
            return ResponseEntity.ok(quantity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}