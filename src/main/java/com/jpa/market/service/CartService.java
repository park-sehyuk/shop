package com.jpa.market.service;

import com.jpa.market.dto.CartDetailDto;
import com.jpa.market.dto.CartItemDto;
import com.jpa.market.dto.OrderDto;
import com.jpa.market.entity.Cart;
import com.jpa.market.entity.CartItem;
import com.jpa.market.entity.Item;
import com.jpa.market.entity.Member;
import com.jpa.market.repository.CartItemRepository;
import com.jpa.market.repository.CartRepository;
import com.jpa.market.repository.ItemRepository;
import com.jpa.market.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    // 장바구니 담기
    public Long addCart(CartItemDto cartItemDto, String longId){

        // 상품 조회
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 업숍니다."));

        // 회원 조회
        Member member = memberRepository.findByLoginId(longId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 업숍니다."));

        // 로그인한 회원의 장바구니가 있는지 확인
        Cart cart = cartRepository.findByMemberId(member.getId());

        // 장바구니가 없으면 새로 생성
        if(cart == null){
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 담고자하는 상품이 장바구니에 있는지 확인
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        // - 있으면 수량만 증가
        if(savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();

            // - 없으면 상품을 새로 추가
        } else{
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }

    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String loginId){

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Cart cart = cartRepository.findByMemberId(member.getId());

        if(cart == null){
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailList(cart.getId());

        return cartDetailDtoList;

    }

    public void updateCartItemCount(Long cartItemId, int count, String loinId) {

        // 수량 검증
        if(count <= 0)
            throw new IllegalArgumentException("최소 1개 이상의 수량을 담아주세요");

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));

        if(!cartItem.getCart().getMember().getLoginId().equals(loinId))
            throw new AccessDeniedException("주문 수정 권한이 없습니다.");

        cartItem.update(count);
    }

    public void deleteCartItem(Long cartItemId, String loginId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("삭제하려는 상품이 존재하지 않습니다."));

        if(!cartItem.getCart().getMember().getLoginId().equals(loginId))
            throw new AccessDeniedException("삭제 권한이 없습니다.");

        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<Long> cartItemIds, String loginId){

        if (cartItemIds == null || cartItemIds.isEmpty()){
            throw new IllegalArgumentException("주문할 상품을 선택해주세요.");
        }

        List<OrderDto> orderDtoList = new ArrayList<>();

        // 장바구니에 있는 상품의 번호만 넘겨받았으므로
        // 진짜 주문에 필요한 정보를 추출
        for (Long cartItemId : cartItemIds){
            // 장바구니에 담겨있는 상품을 조회
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));

            // 권한 확인
            if (!cartItem.getCart().getMember().getLoginId().equals(loginId))
                throw new AccessDeniedException("해당 상품에 대한 주문 권한인 없습니다.");

            // 주문을 하기 위한 dto로 변환
            // 주문에 필요한 정보만 뽑아서 dto에 저장
            // MapStruct 사용 가능
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());

            orderDtoList.add(orderDto);

        }

        // 실제 주문
        Long orderId = orderService.orderMultipleItems(orderDtoList, loginId);

        // 장바구니에서 주문한 목록을 삭제
        for (Long cartItemId : cartItemIds){
            CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow();
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }

}
