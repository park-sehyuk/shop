package com.jpa.market;

import com.jpa.market.constent.ItemSellStatus;
import com.jpa.market.dto.ItemFormDto;
import com.jpa.market.dto.MemberJoinDto;
import com.jpa.market.entity.Item;
import com.jpa.market.entity.Member;
import com.jpa.market.entity.Order;
import com.jpa.market.entity.OrderItem;
import com.jpa.market.repository.ItemRepository;
import com.jpa.market.repository.MemberRepository;
import com.jpa.market.repository.OrderItemRepository;
import com.jpa.market.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
//@Rollback(false) // 테스트가 끝나도 롤백하지 않고 DB에 남겨두도록 함
public class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OrderItemRepository orderItemRepository;

    @PersistenceContext
    EntityManager em;

    // 상품 생성
    public Item createItem(){
        ItemFormDto dto = new ItemFormDto();

        dto.setItemName("테스트 상품");
        dto.setPrice(10000);
        dto.setStockNumber(10);
        dto.setItemDetail("상품 상세 설명");
        dto.setItemSellStatus(ItemSellStatus.SELL);

        return Item.createItem(dto);
    }

    public MemberJoinDto createMember(){
        MemberJoinDto dto = new MemberJoinDto();

        dto.setLoginId("java");
        dto.setPassword("1234");
        dto.setName("강일용");
        dto.setEmail("java@naver.com");
        dto.setAddress("김해시");

        return dto;
    }

    public Member saveMember(){
        MemberJoinDto dto = createMember();

        Member member = Member.createMember(dto, passwordEncoder);

        return memberRepository.save(member);

    }

    public Order createOrder(){
        Member member = this.saveMember();

        Order order = Order.createOrder(member, null);
        for(int i=0; i < 3; i++){
            // itme 객체 생성 및 저장
            Item item = this.createItem();
            itemRepository.save(item);

            // OrderItem 생성
            OrderItem orderItem = OrderItem.createOrderItem(item, 10);

            // 부모-자식 연결(연관관계 매핑)
           // order.getOrderItems().add(orderItem);
            order.addOrderItem(orderItem);
        }

        return orderRepository.save(order);
    }

    @Test
    public void orphanRemovalTest(){
        Order order = this.createOrder();

        order.getOrderItems().remove(0);
        em.flush();
    }

    @Test
    public void cascadeTest(){
        // order객체 생성(member는 일단 null)
        Order order = Order.createOrder(null, null);

        for(int i=0; i < 3; i++){
            // itme 객체 생성 및 저장
            Item item = this.createItem();
            itemRepository.save(item);

            // OrderItem 생성
            OrderItem orderItem = OrderItem.createOrderItem(item, 10);

            // 부모-자식 연결(연관관계 매핑)
            // order.getOrderItems().add(orderItem);
            order.addOrderItem(orderItem);
        }
        // 부모만 저장 -> 매핑되어 이쓰는 곤계까지 자동으로 저장되는지 확인
        orderRepository.save(order);

        em.flush();
        em.clear();

        Order savedOrder = orderRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);

        assertThat(3).isEqualTo(savedOrder.getOrderItems().size());

    }

    @Test
    public void lazyLoding(){
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(EntityNotFoundException::new);

        System.out.println("order class : " + orderItem.getOrder().getClass());

        System.out.println("------------------------");

        orderItem.getOrder().getOrderDate();

        System.out.println("order class : " + orderItem.getOrder().getClass());
        System.out.println("order date : " + orderItem.getOrder().getOrderDate());

    }


}
