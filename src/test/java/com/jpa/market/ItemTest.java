package com.jpa.market;

import com.jpa.market.constent.ItemSellStatus;
import com.jpa.market.dto.ItemFormDto;
import com.jpa.market.entity.Item;
import com.jpa.market.entity.QItem;
import com.jpa.market.repository.ItemRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ItemTest {

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void createItemTest(){
        ItemFormDto dto = new ItemFormDto();

        dto.setItemName("테스트 상품");
        dto.setPrice(10000);
        dto.setStockNumber(10);
        dto.setItemDetail("상품 상세 설명");
        dto.setItemSellStatus(ItemSellStatus.SELL);

        Item item = Item.createItem(dto);

        Item savedItem = itemRepository.save(item);

        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getRegTime()).isNotNull();

        System.out.println(savedItem);
    }


    public void createItemList(){
        for(int i=1; i<=10; i++){
            ItemFormDto dto = new ItemFormDto();

            dto.setItemName("테스트 상품"+i);
            dto.setPrice(10000+i);
            dto.setStockNumber(10+i);
            dto.setItemDetail("상품 상세 설명"+i);
            dto.setItemSellStatus(ItemSellStatus.SELL);

            Item item = Item.createItem(dto);

            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    public void findByItemNameTest(){
        this.createItemList();

        List<Item> itemList = itemRepository.findByItemName("테스트 상품3");

//        for (Item item : itemList){
//            System.out.println(item);
//        }

        // 컬렉션.forEach() : 컬렉션(List, Set, Map)에서 제공하는 메서드

        // itemList.forEach(item -> System.out.println(item));

        //System.out::println ->
        //          item -> System.out.println(item)
        itemList.forEach(System.out::println);
    }

    @Test
    public void findByItemNameOrItemDetailTest(){
        this.createItemList();

        List<Item> itemList = itemRepository.findByItemNameOrItemDetail("테스트 상품4","상품 상세 설명2");

        itemList.forEach(System.out::println);
    }

    @Test
    public void findByItemDetailTest(){
        this.createItemList();

        List<Item> itemList = itemRepository.findByItemDetail("1");

        itemList.forEach(System.out::println);
    }

    @Test
    public void queryDslTest(){
        this.createItemList();

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;

        // 1. JPAQuery를 만들어서 결과를 List에 저장
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                                            .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL)
                                                    .and(qItem.itemDetail.contains("설명")))
                                            .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();

        // 2. List에 바로 저장
        List<Item> list = queryFactory.selectFrom(qItem)
                            .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL)
                                    .and(qItem.itemDetail.contains("설명")))
                            .orderBy(qItem.price.desc())
                            .fetch();

        list.forEach(System.out::println);

    }

}
