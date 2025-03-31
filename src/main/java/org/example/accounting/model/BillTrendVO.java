package org.example.accounting.model;

import lombok.Data;

import java.util.List;

@Data
public class BillTrendVO {
    private List<String> dates;    // 日期列表
    private List<Double> incomes;  // 收入列表
    private List<Double> expenses; // 支出列表

}
