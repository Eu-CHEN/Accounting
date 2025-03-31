package org.example.accounting.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.example.accounting.DO.CartItemDO;

@Mapper
public interface CartItemDAO {

    void addcartItem(CartItemDO cartItemDO);
}
