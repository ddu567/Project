package dev.project.orderservice.service;

import dev.project.orderservice.dto.OrderRequest;
import dev.project.orderservice.dto.PurchaseRequest;
import dev.project.orderservice.repository.OrderRepository;
import dev.project.orderservice.client.ProductServiceClient;
import dev.project.orderservice.repository.WishListRepository;
import dev.project.orderservice.entity.Order;
import dev.project.productservice.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {
    private final ProductServiceClient productServiceClient;
    private final OrderRepository orderRepository;
    private final WishListRepository wishListRepository;

    // ProductServiceClient 주입을 통한 외부 서비스와의 통신
    @Autowired
    public OrderService(ProductServiceClient productServiceClient, OrderRepository orderRepository, WishListRepository wishListRepository) {
        this.productServiceClient = productServiceClient;
        this.orderRepository = orderRepository;
        this.wishListRepository = wishListRepository;
    }

    // 제품 ID를 사용하여 제품 정보를 조회하는 메소드
    public Product fetchProduct(String productId) {
        return productServiceClient.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }

    // 제품 구매를 처리하는 메소드
    public Order purchaseProduct(PurchaseRequest purchaseRequest) {
        Long productId = purchaseRequest.getProductId();
        int quantity = purchaseRequest.getQuantity();

        Product product = productServiceClient.getProductById(productId.toString())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        // 재고 감소 및 제품 정보 업데이트
        product.setStock(product.getStock() - quantity);
        productServiceClient.updateProduct(productId, product);

        // 주문 객체 생성 및 저장
        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setOrderDate(LocalDate.now());
        order.setStatus("배송중");
        return orderRepository.save(order);
    }

    // 위시리스트 항목을 기반으로 주문 생성
    public Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order = orderRepository.save(order);
        orderRequest.getWishListItems().forEach(wishListItem -> {
            Product product = wishListItem.getProduct();
            Integer quantity = wishListItem.getQuantity();
            product.setStock(product.getStock() - quantity);
            productServiceClient.updateProduct(product.getId(), product);
            wishListRepository.delete(wishListItem);
        });
        return order;
    }

    // 주문 취소 처리
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        if (!"배송중".equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order, already shipped");
        }
        order.setStatus("취소완료");
        Product product = order.getProduct();
        product.addStock(order.getQuantity());
        productServiceClient.updateProduct(product.getId(), product);
        return orderRepository.save(order);
    }

    // 반품
    public Order returnOrder(Long orderId) {
        // 주문을 데이터베이스에서 조회합니다.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // 주문 상태가 배송완료인지 확인합니다.
        if (!"배송완료".equals(order.getStatus())) {
            throw new RuntimeException("Cannot return order, not delivered");
        }

        // 주문일로부터 1일이 경과했는지 확인합니다.
        LocalDate deliveryDate = order.getOrderDate().plusDays(1);
        if (!LocalDate.now().isAfter(deliveryDate)) {
            throw new RuntimeException("Cannot return order, return period expired");
        }

        // 제품 정보를 가져옵니다.
        Product product = productServiceClient.getProductById(order.getProduct().getId().toString())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + order.getProduct().getId()));

        // 상품의 재고를 복구하고 제품 정보를 업데이트합니다.
        product.addStock(order.getQuantity());
        productServiceClient.updateProduct(product.getId(), product);

        // 주문 상태를 반품완료로 변경합니다.
        order.setStatus("반품완료");

        // 주문을 저장하고 반환합니다.
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByStatus(String status) {
        // 주문 상태로 주문 목록을 조회합니다.
        return orderRepository.findByStatus(status);
    }
}