package dev.project.orderservice.dto;

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
    private MemberInfoDTO memberInfoDto;  // 사용자 정보
    private List<WishListDTO> wishListItems;  // 위시리스트 항목들
}
