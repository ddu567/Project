package dev.project.orderservice.service;

import dev.project.orderservice.dto.OrderRequest;
import dev.project.orderservice.dto.PurchaseRequest;
import dev.project.orderservice.dto.ProductInfoDTO;
import dev.project.orderservice.dto.WishListDTO;
import dev.project.orderservice.entity.Order;
import dev.project.orderservice.repository.OrderRepository;
import dev.project.orderservice.client.ProductServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final ProductServiceClient productServiceClient;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(ProductServiceClient productServiceClient, OrderRepository orderRepository) {
        this.productServiceClient = productServiceClient;
        this.orderRepository = orderRepository;
    }

    // 제품 정보를 조회하여 반환합니다.
    public ProductInfoDTO fetchProduct(Long productId) {
        Optional<ProductInfoDTO> productOpt = productServiceClient.getProductById(productId);
        if (!productOpt.isPresent()) {
            throw new RuntimeException("Product not found with id: " + productId);
        }
        return productOpt.get();
    }

    // 제품 구매를 처리합니다.
    public Order purchaseProduct(PurchaseRequest purchaseRequest) {
        Long productId = purchaseRequest.getProductId();
        int quantity = purchaseRequest.getQuantity();

        ProductInfoDTO product = fetchProduct(productId);

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        // 제품 재고 감소 후 업데이트
        product.setStock(product.getStock() - quantity);
        productServiceClient.updateProduct(product);  // 업데이트 메서드 수정

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setOrderDate(LocalDate.now());
        order.setStatus("배송중");
        return orderRepository.save(order);
    }

    // 위시리스트 항목을 기반으로 주문을 생성합니다.
    public Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setMemberId(orderRequest.getMemberInfoDto().getId());
        order.setOrderDate(LocalDate.now());
        order.setStatus("배송중");
        orderRepository.save(order);

        for (WishListDTO item : orderRequest.getWishListItems()) {
            ProductInfoDTO product = fetchProduct(item.getProductId());
            product.setStock(product.getStock() - item.getQuantity());
            productServiceClient.updateProduct(product);  // 업데이트 메서드 수정
        }

        return order;
    }

    // 주문 취소 처리합니다.
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        if (!"배송중".equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order, already shipped");
        }
        order.setStatus("취소완료");
        return orderRepository.save(order);
    }

    // 반품 처리합니다.
    public Order returnOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        if (!"배송완료".equals(order.getStatus())) {
            throw new RuntimeException("Cannot return order, not delivered");
        }
        LocalDate returnDeadline = order.getOrderDate().plusDays(30);  // 반품 기한 설정
        if (!LocalDate.now().isBefore(returnDeadline)) {
            throw new RuntimeException("Return period expired");
        }
        order.setStatus("반품완료");
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
}
