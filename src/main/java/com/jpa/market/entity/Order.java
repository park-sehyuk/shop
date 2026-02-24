package com.jpa.market.entity;

import com.jpa.market.constent.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString(exclude = "orderItems")
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity{

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    // 양방향 매핑
    // 주인이 아닌 Order에서도 OrderItem을 조회할 수 있도록 설정
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();


    // 자바 객체를 위해 필요한 메서드
    public void addOrderItem(OrderItem orderItem){
        this.orderItems.add(orderItem); // order가 연관관계로  매핑되어있는 orderItem을 알게하기 위함
        orderItem.setOrder(this);       // OrderItem이 부모인 order를 알게 하기 위해서 사용
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();

        for (OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }

        order.member = member;
        order.orderStatus = OrderStatus.ORDER;
        order.orderDate = LocalDateTime.now();

        return order;
    }

    // 전체 주문 금액 계산
    public int getTotalPrice(){
        int totalPrice = 0;

        for (OrderItem orderItem : orderItems){
            totalPrice += orderItem.getOrderPrice();
        }

        return totalPrice;
    }

    // 주문 취소
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem : orderItems){
            orderItem.cancelOrderItem();;
        }
    }
}
