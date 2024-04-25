package dev.project.controller;

import dev.project.entity.Product;
import dev.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 목록
    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.listAllProducts());
        return "product/products";
    }

    // 상품 상세 조회
    @GetMapping("/products/{id}")
    public String getProduct(@PathVariable Long id, Model model) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product/productDetail";  // 상품 상세 정보 페이지
                })
                .orElse("redirect:/products/not-found");  // 상품이 존재하지 않을 경우의 리다이렉트
    }

    // 구매
    @PostMapping("/order/{id}")
    public String purchaseProduct(@PathVariable Long id, Model model) {
        boolean success = productService.purchaseProduct(id);
        model.addAttribute("orderResult", success);
        return "/order/orderResult";
    }

}
