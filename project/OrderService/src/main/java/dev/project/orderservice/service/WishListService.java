package dev.project.orderservice.service;

import dev.project.orderservice.repository.WishListRepository;
import dev.project.orderservice.dto.WishListDTO;
import dev.project.orderservice.entity.WishList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishListService {

    @Autowired
    private WishListRepository wishListRepository;
    @Autowired
    private CommonService commonService;

    // 사용자의 위시리스트 조회
    public List<WishListDTO> getWishListByUser(Long memberId) {
        return wishListRepository.findByMemberId(memberId).stream()
                .map(wl -> new WishListDTO(wl.getId(), wl.getProductId(), wl.getQuantity()))
                .collect(Collectors.toList());
    }

    // 위시리스트에 상품 추가
    public WishListDTO addToWishList(Long memberId, Long productId, Integer quantity) {
        // 상품 정보 검증
        commonService.getValidProduct(productId, quantity);
        WishList wishList = new WishList();
        wishList.setMemberId(memberId);
        wishList.setProductId(productId);
        wishList.setQuantity(quantity);
        wishList = wishListRepository.save(wishList);
        return new WishListDTO(wishList.getId(), wishList.getProductId(), wishList.getQuantity());
    }

    // 위시리스트 항목 삭제
    public void removeFromWishList(Long wishListId) {
        wishListRepository.deleteById(wishListId);
    }

    // 위시리스트 항목 수정
    public WishListDTO updateWishListItem(Long wishListId, Long productId, Integer newQuantity) {
        WishList wishList = wishListRepository.findById(wishListId).orElseThrow(() -> new RuntimeException("Wish list item not found with ID: " + wishListId));
        wishList.setProductId(productId);
        wishList.setQuantity(newQuantity);
        wishList = wishListRepository.save(wishList);
        return new WishListDTO(wishList.getId(), wishList.getProductId(), wishList.getQuantity());
    }
}
