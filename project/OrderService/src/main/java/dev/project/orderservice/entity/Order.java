package dev.project.orderservice.entity;

import dev.project.productservice.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "Order_purchase")
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;
    private int quantity;
    private LocalDate orderDate;
    private String status; // 예: "배송중", "배송완료", "취소완료", "반품완료"

    public void cancelOrder() {
        if (!"배송중".equals(status)) {
            this.status = "취소완료";
            product.addStock(quantity);
        } else {
            throw new RuntimeException("Cannot cancel order, already in shipping");
        }
    }

    public void returnOrder() {
        if ("배송완료".equals(status) && LocalDate.now().isBefore(orderDate.plusDays(2))) {
            this.status = "반품완료";
            product.addStock(quantity);
        } else {
            throw new RuntimeException("Return not allowed");
        }
    }
}