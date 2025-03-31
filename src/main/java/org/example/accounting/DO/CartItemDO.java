package org.example.accounting.DO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDO {
    private Long id;             // 购物车项ID
    private Long productId;      // 商品ID
    private String name;         // 商品名称
    private String description;  // 商品描述
    private String image;        // 商品图片
    private Double price;    // 商品单价
    private Integer quantity;    // 购买数量
}
