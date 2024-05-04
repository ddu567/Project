package dev.project.orderservice.controller;


import dev.project.userservice.entity.Member;
import dev.project.productservice.entity.Product;
import dev.project.orderservice.entity.WishList;
import org.springframework.web.bind.annotation.*;
import dev.project.userservice.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import dev.project.productservice.service.ProductService;
import dev.project.orderservice.service.WishListService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wishlist")
public class WishListController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WishListService wishListService;

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<List<WishList>> getWishList(@RequestParam Long userId) {
        Member member = new Member(); // 대신 유저 조회 로직 필요
        member.setId(userId);
        List<WishList> wishLists = wishListService.getWishListByUser(member);
        return ResponseEntity.ok(wishLists);
    }

    // 장바구니에 추가
    @PostMapping
    public ResponseEntity<WishList> addWishList(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Integer quantity) {
        Optional<Member> memberOpt = memberService.findById(userId);
        Optional<Product> productOpt = productService.findById(productId);

        if (!memberOpt.isPresent() || !productOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Optional 객체에서 직접 값 추출
        Member member = memberOpt.get();
        Product product = productOpt.get();

        WishList wishList = wishListService.addToWishList(member, product, quantity);
        return ResponseEntity.ok(wishList);
    }

    // 장바구니 삭제
    @DeleteMapping("/{wishListId}")
    public ResponseEntity<?> removeWishList(@PathVariable Long wishListId) {
        wishListService.removeFromWishList(wishListId);
        return ResponseEntity.ok().build();
    }

    // 제품 상세 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<Product> getProductDetails(@PathVariable Long productId) {
        Optional<Product> productOpt = productService.findById(productId);
        return productOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 위시리스트 내 항목 수정
    @PutMapping("/{wishListId}")
    public ResponseEntity<WishList> updateWishListItem(@PathVariable Long wishListId,
                                                       @RequestParam Long productId,
                                                       @RequestParam Integer newQuantity) {
        WishList updatedWishListItem = wishListService.updateWishListItem(wishListId, productId, newQuantity);
        return ResponseEntity.ok(updatedWishListItem);
    }


}