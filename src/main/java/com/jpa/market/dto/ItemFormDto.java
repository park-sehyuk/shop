package com.jpa.market.dto;

import com.jpa.market.constent.ItemSellStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemFormDto {
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

    // 상풍 이미지 정보를 저장할 리스트
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    // 상품 수정시 이미지 아이디를 저장하는 리스트
    private List<Long> itemImgIds = new ArrayList<>();
}
