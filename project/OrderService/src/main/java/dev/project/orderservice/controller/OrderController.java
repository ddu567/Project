package dev.project.orderservice.controller;

import dev.project.orderservice.dto.*;
import dev.project.orderservice.entity.Order;
import dev.project.orderservice.entity.OrderStatus;
import dev.project.orderservice.exception.OrderServiceException;
import dev.project.orderservice.service.CommonService;
import dev.project.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private CommonService commonService;

    // 주문 생성 API, 사용자 ID와 위시리스트를 기반으로 주문 생성
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            if (orderRequest.getWishListItems().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.failure("위시리스트가 비어있습니다."));
            }
            Order order = orderService.createOrder(orderRequest);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (OrderServiceException e) {
            return ResponseEntity.status(e.getStatus()).body(ApiResponse.failure(e.getMessage()));
        }
    }

    // 주문 ID를 이용하여 주문 정보를 조회하는 API
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (OrderServiceException e) {
            return ResponseEntity.status(e.getStatus()).body(ApiResponse.failure(e.getMessage()));
        }
    }

    // 상품 구매 처리 API
    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<Order>> purchaseProduct(@RequestBody PurchaseRequest purchaseRequest) {
        try {
            Order order = orderService.purchaseProduct(purchaseRequest);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (OrderServiceException e) {
            return ResponseEntity.status(e.getStatus()).body(ApiResponse.failure(e.getMessage()));
        }
    }

    // 주문 취소 API
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable Long orderId) {
        try {
            Order canceledOrder = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success(canceledOrder));
        } catch (OrderServiceException e) {
            return ResponseEntity.status(e.getStatus()).body(ApiResponse.failure(e.getMessage()));
        }
    }

    // 주문 반품 API
    @PostMapping("/{orderId}/return")
    public ResponseEntity<ApiResponse<Order>> returnOrder(@PathVariable Long orderId) {
        try {
            Order returnedOrder = orderService.returnOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success(returnedOrder));
        } catch (OrderServiceException e) {
            return ResponseEntity.status(e.getStatus()).body(ApiResponse.failure(e.getMessage()));
        }
    }

    // 주문 상태에 따른 주문 목록을 조회하는 API
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}
