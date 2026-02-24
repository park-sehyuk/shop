package com.jpa.market.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString(exclude = "item")
@Table(name = "item_img")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ItemImg extends BaseEntity{

    @Id
    @Column(name = "item_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // ItemImg에는 create정적 메서드를 사용하지 않음.
    // 1. item에 종속된 엔티티
    // 2. 엔티티 생성할 때 검증되어야 하는 규칙이 없음
    // -> create는 bulider를 이용 예정.
    public void updateItemImg(String imgName, String oriImgName, String imgUrl, String repImgYn){
        this.imgName = imgName;
        this.oriImgName = oriImgName;
        this.imgUrl = imgUrl;
        this.repImgYn = repImgYn;

    }

}
