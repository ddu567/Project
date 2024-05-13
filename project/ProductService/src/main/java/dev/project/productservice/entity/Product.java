package dev.project.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "Product")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private double price;
    private int stock;
    private boolean isReserved;  // 예약 구매 가능 여부
    private LocalDateTime availableFrom; // 예약 구매 시작 시간
    private LocalDateTime availableUntil; // 예약 구매 종료 시간

}
