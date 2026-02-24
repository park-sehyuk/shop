package com.jpa.market.controller;

import com.jpa.market.dto.CartDetailDto;
import com.jpa.market.dto.CartItemDto;
import com.jpa.market.dto.CartOrderDto;
import com.jpa.market.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity addCartItem(@RequestBody @Valid CartItemDto cartItemDto,
                                      Principal principal){

        Long cartItemId = cartService.addCart(cartItemDto, principal.getName());

        return ResponseEntity.ok(cartItemId);
    }

    @GetMapping
    public ResponseEntity getCartList(Principal principal){

        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(principal.getName());

        return ResponseEntity.ok(cartDetailDtoList);
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                         @RequestParam("count") int count, Principal principal) {

        cartService.updateCartItemCount(cartItemId, count, principal.getName());

        return ResponseEntity.ok(cartItemId);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                         Principal principal){

        cartService.deleteCartItem(cartItemId, principal.getName());

        return ResponseEntity.ok(cartItemId);
    }

    @PostMapping("/order")
    public ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                        Principal principal){

        Long orderId = cartService.orderCartItem(cartOrderDto.getCartItemIds(), principal.getName());

        return ResponseEntity.ok(orderId);
    }
}
