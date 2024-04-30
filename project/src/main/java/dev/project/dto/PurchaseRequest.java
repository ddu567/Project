package dev.project.dto;

public class PurchaseRequest {
    private Long productId;
    private int quantity;

    // 생성자, 게터 및 세터
    public PurchaseRequest() {}

    public PurchaseRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
