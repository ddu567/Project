package dev.project.orderservice.service;

import dev.project.orderservice.client.ProductServiceClient;
import dev.project.orderservice.client.UserServiceClient;
import dev.project.orderservice.dto.MemberInfoDTO;
import dev.project.orderservice.dto.ProductInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    @Autowired
    private UserServiceClient userServiceClient;
    @Autowired
    private ProductServiceClient productServiceClient;

    // 회원 ID로 유효한 회원 정보 반환
    public MemberInfoDTO getValidMember(Long memberId) {
        MemberInfoDTO memberInfo = userServiceClient.getMemberById(memberId);
        if (memberInfo == null) {
            throw new IllegalArgumentException("Invalid member ID: " + memberId);
        }
        return memberInfo;
    }

    // 상품 ID와 수량으로 유효한 상품 정보 반환
    public ProductInfoDTO getValidProduct(Long productId, Integer quantity) {
        ProductInfoDTO productInfo = productServiceClient.getProductById(productId);
        if (productInfo == null || productInfo.getStock() < quantity) {
            throw new IllegalArgumentException("Product not available or stock insufficient");
        }
        return productInfo;
    }
}
