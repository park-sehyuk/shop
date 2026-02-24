package com.jpa.market.mapper;

import com.jpa.market.dto.ItemImgDto;
import com.jpa.market.entity.ItemImg;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// 인터페이스의 구현체를 만들도록 지정하고 해당 클래스를 스프링 Bean으로 등록하도록 설정
@Mapper(componentModel = "spring")
public interface ItemImgMapper {

    // 엔티티 -> Dto 변환
    // 결과물타입 메서드이름(원본데이터)
    ItemImgDto entityToDto(ItemImg itemImg);

    @Mapping(target = "id", ignore = true)  // id 제외 (
    @Mapping(target = "item", ignore = true)    // 연관 관계는 서지스에서 따로 설정
    ItemImg dtoToEntity(ItemImgDto itemImgDto);
}
