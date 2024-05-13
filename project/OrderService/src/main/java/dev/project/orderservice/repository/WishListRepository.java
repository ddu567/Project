package dev.project.orderservice.repository;


import dev.project.orderservice.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {

    // 회원별 위시리스트 조회
    List<WishList> findByMemberId(Long memberId);

}