package org.example.accounting.model;

import lombok.Data;

import java.util.List;
@Data
public class BillCategoryStats {
    private List<CategoryAmount> income;  // 收入分类统计
    private List<CategoryAmount> expense; // 支出分类统计
}
