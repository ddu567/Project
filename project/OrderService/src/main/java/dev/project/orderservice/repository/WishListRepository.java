package dev.project.orderservice.repository;


import dev.project.userservice.entity.Member;
import dev.project.orderservice.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findByMember(Member member);
}