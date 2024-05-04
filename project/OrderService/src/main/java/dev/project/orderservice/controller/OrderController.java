package dev.project.orderservice.controller;

import dev.project.orderservice.dto.OrderRequest;
import dev.project.orderservice.dto.PurchaseRequest;
import dev.project.userservice.entity.Member;
import dev.project.orderservice.entity.Order;
import dev.project.orderservice.entity.WishList;
import org.springframework.web.bind.annotation.*;

import dev.project.userservice.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import dev.project.orderservice.service.OrderService;
import dev.project.orderservice.service.WishListService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private WishListService wishListService;

    // 구매하기
    @PostMapping("/purchase")
    public ResponseEntity<Order> purchaseProduct(@RequestBody PurchaseRequest purchaseRequest) {
        Order order = orderService.purchaseProduct(purchaseRequest);
        return ResponseEntity.ok(order);
    }

    // 위시리스트 구매하기
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestParam Long memberId) {
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with ID: " + memberId));

        List<WishList> wishListItems = wishListService.getWishListByUser(member);

        if (wishListItems.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // No items in wish list
        }

        OrderRequest orderRequest = new OrderRequest(member, wishListItems);
        Order order = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(order);
    }


    // 구매 취소
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId) {
        Order order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(order);
    }

    // 반품
    @PatchMapping("/{orderId}/return")
    public ResponseEntity<Order> returnOrder(@PathVariable Long orderId) {
        Order order = orderService.returnOrder(orderId);
        return ResponseEntity.ok(order);
    }

    // 주문 상태 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
}