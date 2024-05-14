package dev.project.orderservice.entity;


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

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "member_id")
    private Long memberId;

    private int quantity;
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 상태 Enum 추가

}