package dev.project.orderservice.service;

import dev.project.orderservice.repository.WishListRepository;
import dev.project.orderservice.client.ProductServiceClient;
import dev.project.orderservice.dto.WishListDTO;
import dev.project.orderservice.entity.WishList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class WishListService {
    private static final Logger logger = LoggerFactory.getLogger(WishListService.class);

    @Autowired
    private WishListRepository wishListRepository;
    @Autowired
    private ProductServiceClient productServiceClient;

    public List<WishListDTO> getWishListByUser(Long memberId) {
        logger.info("Fetching wishlist for member ID: {}", memberId);
        return wishListRepository.findByMemberId(memberId).stream()
                .map(wl -> new WishListDTO(wl.getId(), wl.getProductId(), wl.getQuantity()))
                .collect(Collectors.toList());
    }

    public WishListDTO addToWishList(Long memberId, Long productId, Integer quantity) {
        logger.info("Adding product {} quantity {} to wishlist of member {}", productId, quantity, memberId);
        WishList wishList = new WishList();
        wishList.setMemberId(memberId);
        wishList.setProductId(productId);
        wishList.setQuantity(quantity);
        WishList savedWishList = wishListRepository.save(wishList);
        return new WishListDTO(savedWishList.getId(), savedWishList.getProductId(), savedWishList.getQuantity());
    }

    public void removeFromWishList(Long wishListId) {
        logger.info("Removing wishlist item with ID: {}", wishListId);
        wishListRepository.deleteById(wishListId);
    }

    public WishListDTO updateWishListItem(Long wishListId, Long productId, Integer newQuantity) {
        WishList wishList = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new NoSuchElementException("Wish list item not found with ID: " + wishListId));
        wishList.setProductId(productId);
        wishList.setQuantity(newQuantity);
        wishList = wishListRepository.save(wishList);
        return new WishListDTO(wishList.getId(), wishList.getProductId(), wishList.getQuantity());
    }
}
