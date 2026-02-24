package com.jpa.market.service;

import com.jpa.market.dto.OrderDto;
import com.jpa.market.dto.OrderHistDto;
import com.jpa.market.dto.OrderItemDto;
import com.jpa.market.entity.*;
import com.jpa.market.mapper.OrderItemMapper;
import com.jpa.market.mapper.OrderMapper;
import com.jpa.market.repository.ItemImgRepository;
import com.jpa.market.repository.ItemRepository;
import com.jpa.market.repository.MemberRepository;
import com.jpa.market.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public Long order(OrderDto orderDto, String loginId){

        // 주문할 상품 조회
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(()-> new EntityNotFoundException("해당 제품을 찾을 수 없습니다."));

        // 로그인한 회원 정보 조회
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을수 없습니다. [ 아이디 : "+ loginId + "]"));

        // 주문 처리
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());

        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        // .of : 불변이라서 생성이후 리스트 수정이 불가능함.
        // List<OrderItem> orderItemList = List.of(orderItem);

        Order order = Order.createOrder(member, orderItemList);

        orderRepository.save(order);

        return order.getId();
    }

    // 주문 이력 보기
    public Page<OrderHistDto> getOrderList(String loginId, Pageable pageable){
        // 로그인한 회원의 주문내역을 db에서 조회해서 가져옴(페이징처리)
        Page<Order> ordersPage = orderRepository.findOrders(loginId, pageable);

        List<OrderHistDto> orderHistDtosList = new ArrayList<>();

        // repository에서 조회한 order를 추출
        for (Order order : ordersPage.getContent()){
            // order엔티티 -> orderHistDto로 변환 (각 주문건에 대한 dto를 생성)
            OrderHistDto orderHistDto = orderMapper.entityToDto(order);

            // 해당주문에 포함된 상픔 목록을 조회
            for (OrderItem orderItem : order.getOrderItems()){

                // 주문한 상품의 대표 이미지를 조회
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");

                // 이미지가 없으면 imgUrl을 공백으로 처리
                String imgUrl = (itemImg != null) ? itemImg.getImgUrl() : "";

                // orderItemDto를 생성
                // orderItem엔티티 -> dto로 변환
                OrderItemDto orderItemDto = orderItemMapper.entityToDto(orderItem, imgUrl);

                // 주문에 대한 정보를 담고있는 orderHistDto에 주문 상품을 한건씩 추가
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            // 주문 상품에 대한 정보를 주문 단위의 dto에 저장 -> 하나의 주문이 완성
            orderHistDtosList.add(orderHistDto);

        }

        // 페이징 정보를 유지하면서 리턴
        // (dto리스트, pageable, getTotalElements() : Page가 자동으로 계산해주는 전체 개수)
        return new PageImpl<>(orderHistDtosList, pageable, ordersPage.getTotalElements());
    }

    public void cancelOrder(Long orderId, String loginId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("취소하는 주문이 존재하지 않습니다."));

        // 인가를 확인
        if (!order.getMember().getLoginId().equals(loginId))
            throw new AccessDeniedException("주문 취소 권한이 없습니다.");

        order.cancelOrder();
    }

    public Long orderMultipleItems(List<OrderDto> orderDtoList, String loginId){

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을수 없습니다. [ 아이디 : "+ loginId + "]"));

        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList){
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(()-> new EntityNotFoundException("해당 제품을 찾을 수 없습니다."));

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);

        orderRepository.save(order);

        return order.getId();
    }

}
