package dev.project.orderservice.controller;

import dev.project.orderservice.dto.ApiResponse;
import dev.project.orderservice.dto.PurchaseRequest;
import dev.project.orderservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/decrement")
    public ResponseEntity<?> manageStock(@RequestBody PurchaseRequest request) {
        if (inventoryService.manageStock(request.getProductId(), request.getQuantity())) {
            return ResponseEntity.ok(ApiResponse.success("재고 감소 성공", null));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.failure("재고 부족"));
        }
    }

}