package dev.project.controller;

import dev.project.entity.Product;
import dev.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

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
        Optional<Product> productOptional = productService.getProductById(id);
        if (productOptional.isPresent()) {
            model.addAttribute("product", productOptional.get());
            return "product/productDetail";  // Return the product detail view
        } else {
            return "redirect:/products/not-found";  // Redirect if the product is not found
        }
    }





}
