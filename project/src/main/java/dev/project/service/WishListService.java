package dev.project.service;
import dev.project.entity.Member;
import dev.project.entity.WishList;
import dev.project.repository.WishListRepository;
import dev.project.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WishListService {
    private static final Logger logger = LoggerFactory.getLogger(WishListService.class);

    @Autowired
    private WishListRepository wishListRepository;
    @Autowired
    private ProductService productService;

    public List<WishList> getWishListByUser(Member member) {
        logger.info("Fetching wishlist for member: {}", member.getId());
        return wishListRepository.findByMember(member);
    }

    public WishList addToWishList(Member member, Product product, Integer quantity) {
        logger.info("Adding product {} quantity {} to wishlist of member {}", product.getId(), quantity, member.getId());
        WishList wishList = new WishList();
        wishList.setMember(member);
        wishList.setProduct(product);
        wishList.setQuantity(quantity);
        return wishListRepository.save(wishList);
    }

    // 장바구니 삭제
    public void removeFromWishList(Long wishListId) {
        try {
            logger.info("Removing wishlist item with ID: {}", wishListId);
            wishListRepository.deleteById(wishListId);
        } catch (EmptyResultDataAccessException e) {
            logger.error("Error removing wishlist item: {}", wishListId, e);
            throw new IllegalStateException("The wishlist item to be deleted does not exist.", e);
        }
    }

    // 위시리스트 내 항목 수정
    public WishList updateWishListItem(Long wishListId, Long productId, Integer newQuantity) {
        Optional<WishList> wishListOpt = wishListRepository.findById(wishListId);
        Optional<Product> productOpt = productService.findById(productId);

        if (!wishListOpt.isPresent() || !productOpt.isPresent()) {
            throw new NoSuchElementException("Wish list item or product not found with ID: " + wishListId + ", " + productId);
        }

        WishList wishList = wishListOpt.get();
        Product product = productOpt.get();

        wishList.setProduct(product);
        wishList.setQuantity(newQuantity);

        return wishListRepository.save(wishList);
    }
}
