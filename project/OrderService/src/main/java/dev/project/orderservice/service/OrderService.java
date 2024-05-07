package dev.project.orderservice.service;

import dev.project.orderservice.dto.OrderRequest;
import dev.project.orderservice.dto.PurchaseRequest;
import dev.project.orderservice.dto.ProductInfoDTO;
import dev.project.orderservice.dto.WishListDTO;
import dev.project.orderservice.entity.Order;
import dev.project.orderservice.repository.OrderRepository;
import dev.project.orderservice.client.ProductServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final ProductServiceClient productServiceClient;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(ProductServiceClient productServiceClient, OrderRepository orderRepository) {
        this.productServiceClient = productServiceClient;
        this.orderRepository = orderRepository;
    }

    // Fetches product details from the ProductService
    public ProductInfoDTO fetchProduct(Long productId) {
        try {
            return productServiceClient.getProductById(productId);
        } catch (Exception e) {
            logger.error("Error fetching product details", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found", e);
        }
    }

    // Processes the purchase of a product, managing stock and creating the order
    public Order purchaseProduct(PurchaseRequest purchaseRequest) {
        try {
            Long productId = purchaseRequest.getProductId();
            int quantity = purchaseRequest.getQuantity();

            ProductInfoDTO product = fetchProduct(productId);
            if (product.getStock() < quantity) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - quantity);
            productServiceClient.updateProduct(product);

            Order order = new Order();
            order.setProductId(productId);
            order.setQuantity(quantity);
            order.setOrderDate(LocalDate.now());
            order.setStatus("배송중");
            return orderRepository.save(order);
        } catch (Exception e) {
            logger.error("Error processing purchase order", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Order processing failed", e);
        }
    }

    // Creates an order from the wishlist items provided in the order request
    public Order createOrder(OrderRequest orderRequest) {
        try {
            Order order = new Order();
            order.setMemberId(orderRequest.getMemberInfoDto().getId());
            order.setOrderDate(LocalDate.now());
            order.setStatus("주문완료");
            orderRepository.save(order);

            for (WishListDTO item : orderRequest.getWishListItems()) {
                ProductInfoDTO product = fetchProduct(item.getProductId());
                product.setStock(product.getStock() - item.getQuantity());
                productServiceClient.updateProduct(product);
            }

            return order;
        } catch (Exception e) {
            logger.error("Error creating order from wishlist", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Order creation failed", e);
        }
    }

    // Cancels an order and updates its status in the database
    public Order cancelOrder(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            if (!"배송중".equals(order.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel order, already shipped");
            }
            order.setStatus("취소완료");
            return orderRepository.save(order);
        } catch (Exception e) {
            logger.error("Error canceling order", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Order cancellation failed", e);
        }
    }

    // Returns an order by updating its status if within the allowed return period
    public Order returnOrder(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            LocalDate returnDeadline = order.getOrderDate().plusDays(30);
            if (!LocalDate.now().isBefore(returnDeadline)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return period expired");
            }
            order.setStatus("반품완료");
            return orderRepository.save(order);
        } catch (Exception e) {
            logger.error("Error returning order", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Order return failed", e);
        }
    }

    // Retrieves all orders matching a particular status
    public List<Order> getOrdersByStatus(String status) {
        try {
            return orderRepository.findByStatus(status);
        } catch (Exception e) {
            logger.error("Error retrieving orders by status", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving orders", e);
        }
    }
}
