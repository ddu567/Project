package dev.project.orderservice.dto;

import dev.project.userservice.entity.Member;
import dev.project.orderservice.entity.WishList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Member member;
    private List<WishList> wishListItems;
}