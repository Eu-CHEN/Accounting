package org.example.accounting.DAO;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.accounting.util.CartAddRequest;

@Mapper
public interface ShopCartDAO {

    /**
     * 添加购物车
     * @param request
     */
    @Insert("INSERT INTO shoppingcart (productId, quantity) " +
            "VALUES (#{productId}, #{quantity}) " +
            "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)")
    void addToCart(CartAddRequest request);
}
