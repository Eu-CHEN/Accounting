package org.example.accounting.util;

import lombok.Data;

@Data
public class CartAddRequest {
    private Long productId;      // 商品ID
    private Integer quantity;    //购买数量
}
