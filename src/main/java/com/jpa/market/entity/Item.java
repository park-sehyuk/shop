package com.jpa.market.entity;

import com.jpa.market.config.exception.OutOfStockException;
import com.jpa.market.constent.ItemSellStatus;
import com.jpa.market.dto.ItemFormDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity     // 엔티티임을 명시
@Table(name = "item")   // 생성되는 테이블명
@Getter     // setter를 사용하지 않음
@ToString(exclude = "itemImgs")   // 매핑관계에서는 사용하지 않는것이 좋으나 테스트를 위해 작성
// JPA가 객체를 생성할 수는 있지만, 무분별한 객체 생성을 방지함
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Item extends BaseEntity{

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber;

    @Lob    // 대용량 텍스트 파일
    @Column(nullable = false)
    private String itemDetail;

    @Enumerated(EnumType.STRING)    // enum타입 매핑
    private ItemSellStatus itemSellStatus;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc") // item.getItemImgs()를 호출할 때 이미지를 정렬하여 가져오도록 설정
    private List<ItemImg> itemImgs = new ArrayList<>();


    // 객체 생성 메서드
    public static Item createItem(ItemFormDto itemFormDto){
        Item item = new Item();
        item.itemName = itemFormDto.getItemName();
        item.price = itemFormDto.getPrice();
        item.stockNumber = itemFormDto.getStockNumber();
        item.itemDetail = itemFormDto.getItemDetail();
        item.itemSellStatus = itemFormDto.getItemSellStatus();

        return item;
    }

    // 상품 수정
    public void updateItem(ItemFormDto itemFormDto){
        // this : JPA가 DB에서 조회해온 상품 객체를 말함
        this.itemName = itemFormDto.getItemName();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;

        if(restStock < 0){
            throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량 : "+ this.stockNumber + ")");
        }

        this.stockNumber = restStock;

        if(this.stockNumber == 0 )
            this.itemSellStatus = ItemSellStatus.SOLD_OUT;
    }

    // 재고 증가
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;

        if(this.stockNumber > 0)
            this.itemSellStatus = ItemSellStatus.SELL;
    }

}
