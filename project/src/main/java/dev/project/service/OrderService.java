package dev.project.service;

import dev.project.dto.OrderRequest;
import dev.project.dto.PurchaseRequest;
import dev.project.entity.Member;
import dev.project.entity.Order;
import dev.project.entity.WishList;
import dev.project.repository.OrderRepository;
import dev.project.entity.Product;
import dev.project.repository.ProductRepository;
import dev.project.repository.WishListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WishListRepository wishListRepository;

    public Order purchaseProduct(PurchaseRequest purchaseRequest) {
        // 요청에서 제품 ID와 구매 수량을 가져옵니다.
        Long productId = purchaseRequest.getProductId();
        int quantity = purchaseRequest.getQuantity();

        // 제품 ID를 사용하여 제품 정보를 조회합니다.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // 제품의 재고를 확인하고 구매 가능한지 검사합니다.
        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        // 주문 생성
        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setOrderDate(LocalDate.now());
        order.setStatus("배송중");

        // 주문을 저장하고 제품의 재고를 감소시킵니다.
        order = orderRepository.save(order);
        product.removeStock(quantity);
        productRepository.save(product);

        return order;
    }

    // 위시리스트 구매 + 장바구니 삭제
    public Order createOrder(OrderRequest orderRequest) {
        // Create order
        Order order = new Order();
        // Set other order details as needed
        order = orderRepository.save(order);

        // Remove wish list items and update product quantities
        for (WishList wishListItem : orderRequest.getWishListItems()) {
            Product product = wishListItem.getProduct();
            Integer quantity = wishListItem.getQuantity();
            product.setStock(product.getStock() - quantity); // Adjust product stock
            productService.save(product); // Save updated product stock
            wishListRepository.delete(wishListItem); // Remove wish list item
        }

        return order;
    }

    // 주문 취소
    public Order cancelOrder(Long orderId) {
        // 주문을 데이터베이스에서 조회합니다.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // 주문 상태가 배송중인지 확인합니다.
        if (!"배송중".equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order, already shipped");
        }

        // 주문 상태를 취소로 변경하고 제품의 재고를 복구합니다.
        order.setStatus("취소완료");
        Product product = order.getProduct();
        product.addStock(order.getQuantity());
        productRepository.save(product);

        // 주문을 저장하고 반환합니다.
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

        // 상품의 재고를 복구하고 주문 상태를 반품완료로 변경합니다.
        Product product = order.getProduct();
        product.addStock(order.getQuantity());
        productRepository.save(product);
        order.setStatus("반품완료");

        // 주문을 저장하고 반환합니다.
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByStatus(String status) {
        // 주문 상태로 주문 목록을 조회합니다.
        return orderRepository.findByStatus(status);
    }
}