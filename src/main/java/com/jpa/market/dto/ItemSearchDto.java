package com.jpa.market.dto;

import com.jpa.market.constent.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    // 현재 날짜, 시간과 등록일을 비교해서 조회
    private String searchDateType;

    // 상품의 판매 상태를 기준으로 조회
    private ItemSellStatus searchSellStatus;

    // 상품을 조회할 유형(상품명, 등록한 사람...)
    private String searchBy;

    // 조회할 검색어
    private String searchQuery = "";

}
