package com.jpa.market.dto;

import com.jpa.market.constent.ItemSellStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCreateDto {
    private Long id;

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String itemName;

    @NotNull(message = "가격은 필수 입력값입니다.")
    private Integer price;

    @NotBlank(message = "상품 상세 설명은 필수 입력값입니다.")
    private String itemDetail;

    @NotNull(message = "상품 재고량은 필수 입력값입니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;
}
