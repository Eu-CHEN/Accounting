package org.example.accounting.DO;
import lombok.Data;

@Data
public class ProductDO {
    private Integer id;             // 商品ID
    private String name;         // 商品名称
    private Double price;    // 商品价格
    private String description;  // 商品描述
    private String image;        // 商品图片URL
}
