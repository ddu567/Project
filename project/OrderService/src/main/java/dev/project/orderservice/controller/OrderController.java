package dev.project.orderservice.controller;

import dev.project.orderservice.client.ProductServiceClient;
import dev.project.orderservice.client.UserServiceClient;
import dev.project.orderservice.dto.*;
import dev.project.orderservice.entity.Order;
import dev.project.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserServiceClient userServiceClient;
    @Autowired
    private ProductServiceClient productServiceClient; // 상품 서비스 클라이언트

    // 주문 생성 (재고 확인 후 생성)
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestParam Long memberId, @RequestBody List<WishListDTO> wishListItems) {
        if (wishListItems.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // 장바구니가 비어 있음
        }

        MemberInfoDTO memberInfo = userServiceClient.getMemberById(memberId);
        if (memberInfo == null) {
            return ResponseEntity.notFound().build(); // 회원 정보가 없는 경우
        }

        for (WishListDTO item : wishListItems) {
            ProductInfoDTO productInfo = productServiceClient.getProductById(item.getProductId());
            if (productInfo.getStock() < item.getQuantity()) {
                return ResponseEntity.badRequest().body(null); // 재고 부족
            }
        }

        // 주문 객체 생성 및 저장 로직
        OrderRequest orderRequest = new OrderRequest(memberInfo, wishListItems);
        Order order = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(order);
    }

    // 구매하기
    @PostMapping("/purchase")
    public ResponseEntity<Order> purchaseProduct(@RequestBody PurchaseRequest purchaseRequest) {
        Order order = orderService.purchaseProduct(purchaseRequest);
        return ResponseEntity.ok(order);
    }

    // 주문 취소
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
