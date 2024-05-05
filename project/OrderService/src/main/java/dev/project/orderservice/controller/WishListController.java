package dev.project.orderservice.controller;

import dev.project.orderservice.client.MemberServiceClient;
import dev.project.orderservice.client.ProductServiceClient;
import dev.project.orderservice.dto.MemberInfoDTO;
import dev.project.orderservice.dto.ProductInfoDTO;
import dev.project.orderservice.dto.WishListDTO;
import dev.project.orderservice.service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wishlist")
public class WishListController {

    @Autowired
    private MemberServiceClient memberServiceClient;
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
    @PostMapping
    public ResponseEntity<WishListDTO> addWishList(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Integer quantity) {
        Optional<MemberInfoDTO> memberInfoOpt = Optional.ofNullable(memberServiceClient.getMemberById(userId));
        Optional<ProductInfoDTO> productInfoOpt = productServiceClient.getProductById(productId);

        if (!memberInfoOpt.isPresent() || !productInfoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        WishListDTO wishList = wishListService.addToWishList(userId, productId, quantity);
        return ResponseEntity.ok(wishList);
    }

    // 제품 상세 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductInfoDTO> getProductDetails(@PathVariable Long productId) {
        Optional<ProductInfoDTO> productInfoOpt = productServiceClient.getProductById(productId);
        return productInfoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 위시리스트 내 항목 수정
    @PutMapping("/{wishListId}")
    public ResponseEntity<WishListDTO> updateWishListItem(@PathVariable Long wishListId, @RequestParam Long productId, @RequestParam Integer newQuantity) {
        Optional<ProductInfoDTO> productInfoOpt = productServiceClient.getProductById(productId);

        if (!productInfoOpt.isPresent()) {
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
