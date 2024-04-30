package dev.project.repository;


import dev.project.entity.Member;
import dev.project.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findByMember(Member member);
}