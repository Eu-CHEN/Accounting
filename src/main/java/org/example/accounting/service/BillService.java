package org.example.accounting.service;

import org.example.accounting.DO.BillDO;
import org.example.accounting.DO.Categories;
import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.model.BillCategoryStats;
import org.example.accounting.model.BillStatistics;
import org.example.accounting.model.BillTrendVO;
import org.example.accounting.util.PageResult;

import java.util.List;

public interface BillService {

    PageResult pageQuery(PageQueryDO pageQueryDO);

    //添加账单
    void addBill(BillDO billDO);

    //修改账单
    void updateBill(BillDO billDO);

    //删除账单
    void deleteBill(Integer id);

    List<String> getTypes();

    List<String> getCategories();

    List<Categories> getAllCategories();

    void addCategories(Categories categories);

    void deleteCategories(Categories categories);

    void updateCategories(Categories categories);

    BillStatistics getBillSummary(String billStatistics, String username);


    
    BillCategoryStats getCategoryStats(String username, String time);

    BillTrendVO getTrend(String username, String time);
}
