package dev.project.orderservice.controller;

import dev.project.orderservice.client.UserServiceClient;
import dev.project.orderservice.client.ProductServiceClient;
import dev.project.orderservice.dto.MemberInfoDTO;
import dev.project.orderservice.dto.ProductInfoDTO;
import dev.project.orderservice.dto.WishListDTO;
import dev.project.orderservice.service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishListController {

    @Autowired
    private UserServiceClient userServiceClient;
    @Autowired
    private ProductServiceClient productServiceClient;
    @Autowired
    private WishListService wishListService;

    // 장바구니 조회

    @GetMapping
    public ResponseEntity<List<WishListDTO>> getWishList(@RequestParam Long userId) {
        List<WishListDTO> wishLists = wishListService.getWishListByUser(userId);
        return ResponseEntity.ok(wishLists);
    }

    // 장바구니에 추가
    @PostMapping("/add")
    public ResponseEntity<WishListDTO> addWishList(@RequestParam Long userId, Long productId, Integer quantity) {
        MemberInfoDTO memberInfo = userServiceClient.getMemberById(userId);
        ProductInfoDTO productInfo = productServiceClient.getProductById(productId);

        if (memberInfo == null || productInfo == null) {
            return ResponseEntity.notFound().build();
        }

        WishListDTO wishList = wishListService.addToWishList(userId, productId, quantity);
        return ResponseEntity.ok(wishList);
    }

    // 제품 상세 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductInfoDTO> getProductDetails(@PathVariable Long productId) {
        ProductInfoDTO productInfo = productServiceClient.getProductById(productId);
        if (productInfo != null) {
            return ResponseEntity.ok(productInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 위시리스트 내 항목 수정
    @PutMapping("/{wishListId}")
    public ResponseEntity<WishListDTO> updateWishListItem(@PathVariable Long wishListId, @RequestParam Long productId, @RequestParam Integer newQuantity) {
        ProductInfoDTO productInfo = productServiceClient.getProductById(productId);
        if (productInfo == null) {
            return ResponseEntity.notFound().build();
        }

        WishListDTO updatedWishListItem = wishListService.updateWishListItem(wishListId, productId, newQuantity);
        return ResponseEntity.ok(updatedWishListItem);
    }

    // 장바구니 삭제
    @DeleteMapping("/{wishListId}")
    public ResponseEntity<?> removeWishList(@PathVariable Long wishListId) {
        try {
            wishListService.removeFromWishList(wishListId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
