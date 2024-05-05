package dev.project.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInfoDTO {
    private Long id;
    private String name;
    private int stock; // 재고 수량

    public ProductInfoDTO(Long id, String name, int stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }
}