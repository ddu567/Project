package dev.project.orderservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "WishList")
@NoArgsConstructor
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId; // 회원 ID 저장용 필드

    @Column(name = "product_id")
    private Long productId; // 제품 ID 저장용 필드

    private Integer quantity; // 수량

}
