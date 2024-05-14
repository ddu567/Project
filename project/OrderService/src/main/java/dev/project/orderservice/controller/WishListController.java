package dev.project.orderservice.controller;

import dev.project.orderservice.dto.ProductInfoDTO;
import dev.project.orderservice.dto.WishListDTO;
import dev.project.orderservice.service.CommonService;
import dev.project.orderservice.service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishListController {

    @Autowired
    private WishListService wishListService;
    @Autowired
    private CommonService commonService;

    // 사용자 ID를 이용하여 해당 사용자의 위시리스트를 조회하는 API
    @GetMapping
    public ResponseEntity<List<WishListDTO>> getWishList(@RequestParam Long userId) {
        try {
            // 회원 정보 유효성 검증
            commonService.getValidMember(userId);
            // 위시리스트 항목 조회
            List<WishListDTO> wishLists = wishListService.getWishListByUser(userId);
            return ResponseEntity.ok(wishLists);
        } catch (IllegalArgumentException e) {
            // 회원 정보가 유효하지 않은 경우
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 위시리스트에 상품 추가 API, 상품 정보와 수량을 지정하여 추가
    @PostMapping("/add")
    public ResponseEntity<WishListDTO> addWishList(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Integer quantity) {
        try {
            // 회원 및 상품 정보 검증
            commonService.getValidMember(userId);
            commonService.getValidProduct(productId, quantity);
            // 위시리스트에 항목 추가
            WishListDTO wishList = wishListService.addToWishList(userId, productId, quantity);
            return ResponseEntity.ok(wishList);
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 정보 제공 시 오류 메시지 반환
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 위시리스트 항목 수정 API, 위시리스트 ID와 수정할 상품 정보 및 수량을 통해 수정
    @PutMapping("/{wishListId}")
    public ResponseEntity<WishListDTO> updateWishListItem(@PathVariable Long wishListId, @RequestParam Long productId, @RequestParam Integer newQuantity) {
        try {
            // 상품 정보 검증
            commonService.getValidProduct(productId, newQuantity);
            // 위시리스트 항목 수정
            WishListDTO updatedWishListItem = wishListService.updateWishListItem(wishListId, productId, newQuantity);
            return ResponseEntity.ok(updatedWishListItem);
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 상품 정보 제공 시
            return ResponseEntity.badRequest().body(null);
        }
    }


    // 위시리스트 항목 삭제 API, 위시리스트 ID를 통해 해당 항목을 삭제
    @DeleteMapping("/{wishListId}")
    public ResponseEntity<?> removeWishList(@PathVariable Long wishListId) {
        try {
            // 위시리스트 항목 삭제
            wishListService.removeFromWishList(wishListId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // 삭제 실패 시
            return ResponseEntity.notFound().build();
        }
    }

}