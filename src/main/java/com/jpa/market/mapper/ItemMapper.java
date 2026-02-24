package com.jpa.market.mapper;

import com.jpa.market.dto.ItemFormDto;
import com.jpa.market.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// uses : ItemImg의 변환을 ItemImgMapper에 위임
@Mapper(componentModel = "spring", uses = {ItemImgMapper.class})
public abstract interface ItemMapper {

    @Mapping(target = "itemImgDtoList", source = "itemImgs")
    ItemFormDto entityToDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itemImgs", source = "itemImgDtoList")
    Item dtoToEntity(ItemFormDto itemFormDto);
}
