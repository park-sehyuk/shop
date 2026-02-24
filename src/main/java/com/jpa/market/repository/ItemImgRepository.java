package com.jpa.market.repository;

import com.jpa.market.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    // 1. 쿼리메서드(findBy..) 2. JPQL(@Query) 3.QueryDSL (Q클래스)

    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

    // 어떤 상품의 대표 이미지 조회
    ItemImg findByItemIdAndRepImgYn(Long itemId, String repImgYn);

}
