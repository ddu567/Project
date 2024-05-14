package dev.project.orderservice.service;

import dev.project.orderservice.client.ProductServiceClient;
import dev.project.orderservice.dto.OrderRequest;
import dev.project.orderservice.dto.PurchaseRequest;
import dev.project.orderservice.dto.ProductInfoDTO;
import dev.project.orderservice.dto.WishListDTO;
import dev.project.orderservice.entity.Order;
import dev.project.orderservice.entity.OrderStatus;
import dev.project.orderservice.exception.OrderServiceException;
import dev.project.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final CommonService commonService;
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Autowired
    public OrderService(CommonService commonService, OrderRepository orderRepository, ProductServiceClient productServiceClient) {
        this.commonService = commonService;
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
    }

    // 주문 조회 메서드
    public Order getOrderById(Long orderId) throws OrderServiceException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderServiceException("주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    // 주문 생성 및 상품 구매 처리 메서드
    @Transactional
    public Order processOrder(OrderRequest orderRequest, PurchaseRequest purchaseRequest) throws OrderServiceException {
        Order order = new Order();
        if (orderRequest != null) {
            order.setMemberId(orderRequest.getMemberInfoDto().getId());
            order.setOrderDate(LocalDate.now());
            order.setStatus(OrderStatus.ORDERED);
            orderRepository.save(order);

            for (WishListDTO item : orderRequest.getWishListItems()) {
                Long productId = item.getProductId();
                int quantity = item.getQuantity();
                validateInput(productId, quantity);
                commonService.getValidProduct(productId, quantity);

                ProductInfoDTO product = productServiceClient.getProductById(productId);
                productServiceClient.updateProduct(productId, product);
            }
        } else if (purchaseRequest != null) {
            Long productId = purchaseRequest.getProductId();
            int quantity = purchaseRequest.getQuantity();

            validateInput(productId, quantity);
            ProductInfoDTO product = commonService.getValidProduct(productId, quantity);

            order.setProductId(productId);
            order.setQuantity(quantity);
            order.setOrderDate(LocalDate.now());
            order.setStatus(OrderStatus.SHIPPED);
            productServiceClient.updateProduct(productId, product);

            orderRepository.save(order);
        } else {
            throw new OrderServiceException("주문 생성 또는 구매 요청 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        return order;
    }

    // 주문 취소 처리 메서드
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (OrderStatus.SHIPPED.equals(order.getStatus())) {
            logger.error("이미 배송된 주문은 취소할 수 없습니다.");
            throw new OrderServiceException("이미 배송된 주문은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    // 주문 반품 처리 메서드
    @Transactional
    public Order returnOrder(Long orderId) {
        Order order = getOrderById(orderId);
        LocalDate returnDeadline = order.getOrderDate().plusDays(30);
        if (LocalDate.now().isAfter(returnDeadline)) {
            logger.error("반품 기간이 만료되었습니다.");
            throw new OrderServiceException("반품 기간이 만료되었습니다.", HttpStatus.BAD_REQUEST);
        }
        order.setStatus(OrderStatus.RETURNED);
        return orderRepository.save(order);
    }

    // 특정 주문 상태에 따른 주문 목록 조회 메서드
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // 입력 검증 강화 메서드
    private void validateInput(Long productId, Integer quantity) throws IllegalArgumentException {
        if (productId == null || quantity == null || quantity <= 0) {
            logger.error("제품 ID 또는 수량이 유효하지 않습니다.");
            throw new IllegalArgumentException("제품 ID 또는 수량이 유효하지 않습니다.");
        }
    }
}
