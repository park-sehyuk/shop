package com.jpa.market;

import com.jpa.market.dto.ItemFormDto;
import com.jpa.market.dto.ItemImgDto;
import com.jpa.market.entity.Item;
import com.jpa.market.mapper.ItemMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MapperTest {

    @Autowired
    ItemMapper itemMapper;

    @Test
    public void itemMappingTest(){

        ItemFormDto dto = new ItemFormDto();
        dto.setItemName("매핑 상품");
        dto.setPrice(1000);

        ItemImgDto imgDto = new ItemImgDto();
        imgDto.setOriImgName("test.jpg");
        imgDto.setImgUrl("/img/test.jpg");

        dto.getItemImgDtoList().add(imgDto);

        Item item = itemMapper.dtoToEntity(dto);

        // 확인
        assertThat(item.getItemName()).isEqualTo(dto.getItemName());
        assertThat(item.getPrice()).isEqualTo(dto.getPrice());

        assertThat(item.getItemImgs()).isNotNull();
        assertThat(item.getItemImgs()).size().isEqualTo(1);
        assertThat(item.getItemImgs().get(0).getOriImgName()).isEqualTo("test.jpg");

        System.out.println("상품명 : "+ item.getItemName());
        System.out.println("이미지 개수 : "+ item.getItemImgs().size());
    }

}
