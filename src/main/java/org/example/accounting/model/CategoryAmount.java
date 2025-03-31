package org.example.accounting.model;
import lombok.Data;

@Data
public class CategoryAmount {
    private String name;    // 分类名称
    private Double value;   // 金额
}