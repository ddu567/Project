package dev.project.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSession {

    private Long sessionId;
    private Long productId;
    private int quantity;
    private LocalDateTime startTime;
    private boolean completed;

}