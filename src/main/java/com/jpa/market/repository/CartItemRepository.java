package com.jpa.market.repository;

import com.jpa.market.dto.CartDetailDto;
import com.jpa.market.dto.CartItemDto;
import com.jpa.market.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndItemId(Long cartId, Long itemId);


    @Query("select new com.jpa.market.dto.CartDetailDto(" +
            "ci.id, i.itemName, i.price, ci.count, im.imgUrl) " +
            "from CartItem ci " +
            "join ci.item i " +
            "join i.itemImgs im "+
            "where ci.cart.id = :cartId " +
            "and im.repImgYn = 'Y' " +
            "order by ci.regTime desc")
    List<CartDetailDto> findCartDetailList(@Param("cartId") Long cartId);

}
