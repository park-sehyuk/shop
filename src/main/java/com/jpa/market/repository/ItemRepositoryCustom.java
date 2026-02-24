package com.jpa.market.repository;

import com.jpa.market.dto.ItemAdminListDto;
import com.jpa.market.dto.ItemSearchDto;
import com.jpa.market.dto.MainItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// jpa는 인터페이스 + 구현클래스만 자동으로 연결 처리
// coustom을 만드는 이유는 1. 복잡한 퀴리, 2.
public interface ItemRepositoryCustom {

    // Pageable : JPA에서 제공하는 페이징과 정렬에 대한 정보가 담겨있는 인터페이스
    //              page(현재 페이지번호), size(한 페이지에 나타낼 글 수), sort(정렬)....
    // Pageable로 요청을 하고, 페이징에 대한 응답으로 Page가 응답
    // 리턴을 page로 하면 현재 페이지의 데이터 + 전체 페이지 수 + 페이징...등의 모든 정보를 담아서 리턴
    Page<ItemAdminListDto> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
