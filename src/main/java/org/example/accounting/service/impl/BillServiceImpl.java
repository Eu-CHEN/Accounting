package org.example.accounting.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.example.accounting.DAO.BillDAO;
import org.example.accounting.DAO.BillTypesDAO;
import org.example.accounting.DAO.CategoriesDAO;
import org.example.accounting.DO.BillDO;
import org.example.accounting.DO.BillTypes;
import org.example.accounting.DO.Categories;
import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.model.BillCategoryStats;
import org.example.accounting.model.BillStatistics;
import org.example.accounting.model.BillTrendVO;
import org.example.accounting.model.CategoryAmount;
import org.example.accounting.service.BillService;
import org.example.accounting.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillDAO billDAO;

    @Autowired
    private CategoriesDAO categoriesDAO;

    @Autowired
    private BillTypesDAO billTypesDAO;


    @Override
    public PageResult pageQuery(PageQueryDO queryDO) {
        PageHelper.startPage(queryDO.getPagenum(), queryDO.getPagesize());
        Page<BillDO> page = billDAO.pageQuery(queryDO);
        return new PageResult(page.getTotal(), page.getPageNum(), page.getResult());
    }

    @Override
    public void addBill(BillDO billDO) {
       billDAO.addBill(billDO);
    }

    /**
     * 根据id修改账单
     * @param billDO
     */
    @Override
    public void updateBill(BillDO billDO) {
        billDAO.updateBill(billDO);
    }

    @Override
    public void deleteBill(Integer id) {
        billDAO.deleteBill(id);
    }

    @Override
    public List<String> getTypes() {
        List<Categories> list = categoriesDAO.getTypes();
        List<String> types = new ArrayList<>();
        for (Categories c : list) {
            types.add(c.getBillfication());
        }
        return types;
    }

    @Override
    public List<String> getCategories() {
        List<BillTypes> list = billTypesDAO.getCategories();
        List<String> categories = new ArrayList<>();
        for (BillTypes b : list) {
            categories.add(b.getBilltype());
        }
        return categories;
    }

    @Override
    public List<Categories> getAllCategories() {
        List<Categories> categories = categoriesDAO.getTypes();
        return categories;
    }

    @Override
    public void addCategories(Categories categories) {
        categoriesDAO.addCategories(categories);
    }

    @Override
    public void deleteCategories(Categories categories) {
        categoriesDAO.deleteCategories(categories);
    }

    @Override
    public void updateCategories(Categories categories) {
        categoriesDAO.updateCategories(categories);
    }

    /**
     * 根据用户名查询总收支
     * @param username
     * @param time
     * @return
     */
    @Override
    public BillStatistics getBillSummary(String username, String time) {
        // 获取该用户的所有账单
        List<BillDO> list = billDAO.getBillByUsername(username);

        // 创建统计对象
        BillStatistics statistics = new BillStatistics();

        // 如果列表为空，返回默认值
        if (list == null || list.isEmpty()) {
            statistics.setTotalIncome("0.00");
            statistics.setTotalExpense("0.00");
            statistics.setBalance("0.00");
            return statistics;
        }

        // 定义收入和支出
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        // 获取当前时间
        LocalDate now = LocalDate.now();
        LocalDate startDate;

        // 根据时间范围参数设置起始日期
        switch (time) {
            case "week":
                startDate = now.minusWeeks(1);
                break;
            case "month":
                startDate = now.withDayOfMonth(1);
                break;
            case "year":
                startDate = now.withDayOfYear(1);
                break;
            default:
                startDate = now.withDayOfMonth(1); // 默认按月
        }

        // 遍历账单列表进行统计
        for (BillDO bill : list) {
            // 转换账单日期字符串为LocalDate对象
            LocalDate billDate = bill.getAddTime().toLocalDate();

            // 判断是否在时间范围内
            if (billDate.isEqual(startDate) || billDate.isAfter(startDate)) {
                BigDecimal amount = new BigDecimal(bill.getAmount());

                // 根据账单类型进行统计
                if ("收入".equals(bill.getBilltype())) {
                    totalIncome = totalIncome.add(amount);
                } else if ("支出".equals(bill.getBilltype())) {
                    totalExpense = totalExpense.add(amount);
                }
            }
        }

        // 计算结余
        BigDecimal balance = totalIncome.subtract(totalExpense);

        // 设置统计结果，保留两位小数
        statistics.setTotalIncome(totalIncome.setScale(2, RoundingMode.HALF_UP).toString());
        statistics.setTotalExpense(totalExpense.setScale(2, RoundingMode.HALF_UP).toString());
        statistics.setBalance(balance.setScale(2, RoundingMode.HALF_UP).toString());

        return statistics;
    }





    @Override
    public BillCategoryStats getCategoryStats(String username, String time) {
        // 获取时间范围
        LocalDate now = LocalDate.now();
        LocalDate startDate = getStartDate(now, time);

        // 获取该用户在时间范围内的所有账单
        List<BillDO> bills = billDAO.getBillsByUsernameAndTimeRange(username, startDate, now);

        // 分别统计收入和支出
        Map<String, Double> incomeMap = new HashMap<>();
        Map<String, Double> expenseMap = new HashMap<>();

        for (BillDO bill : bills) {
            String category = bill.getBillfication();
            Double amount = bill.getAmount();

            if ("收入".equals(bill.getBilltype())) {
                incomeMap.merge(category, amount, Double::sum);
            } else if ("支出".equals(bill.getBilltype())) {
                expenseMap.merge(category, amount, Double::sum);
            }
        }

        // 转换为前端需要的格式
        BillCategoryStats stats = new BillCategoryStats();
        stats.setIncome(convertMapToList(incomeMap));
        stats.setExpense(convertMapToList(expenseMap));

        return stats;
    }

    @Override
    public BillTrendVO getTrend(String username, String time) {
        // 获取时间范围
        LocalDate now = LocalDate.now();
        LocalDate startDate = getStartDate(now, time);

        // 获取该用户在时间范围内的所有账单
        List<BillDO> bills = billDAO.getBillsByUsernameAndTimeRange(username, startDate, now);

        // 准备日期列表和对应的收支数据
        List<String> dates = new ArrayList<>();
        Map<String, Double> incomeMap = new HashMap<>();
        Map<String, Double> expenseMap = new HashMap<>();

        // 生成日期列表
        for (LocalDate date = startDate; !date.isAfter(now); date = date.plusDays(1)) {
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            dates.add(dateStr);
            incomeMap.put(dateStr, 0.0);
            expenseMap.put(dateStr, 0.0);
        }

        // 统计每天的收支
        for (BillDO bill : bills) {
            String dateStr = (bill.getAddTime().toLocalDate()).toString(); // 假设addTime格式为"yyyy-MM-dd HH:mm:ss"
            Double amount = bill.getAmount();

            if ("收入".equals(bill.getBilltype())) {
                incomeMap.merge(dateStr, amount, Double::sum);
            } else if ("支出".equals(bill.getBilltype())) {
                expenseMap.merge(dateStr, amount, Double::sum);
            }
        }

        // 构建返回对象
        BillTrendVO trend = new BillTrendVO();
        trend.setDates(dates);
        trend.setIncomes(dates.stream().map(incomeMap::get).collect(Collectors.toList()));
        trend.setExpenses(dates.stream().map(expenseMap::get).collect(Collectors.toList()));

        return trend;
    }

    // 辅助方法：根据时间范围获取起始日期
    private LocalDate getStartDate(LocalDate now, String time) {
        return switch (time) {
            case "week" -> now.minusWeeks(1);
            case "month" -> now.withDayOfMonth(1);
            case "year" -> now.withDayOfYear(1);
            default -> now.withDayOfMonth(1); // 默认按月
        };
    }

    // 辅助方法：将Map转换为List<CategoryAmount>
    private List<CategoryAmount> convertMapToList(Map<String, Double> map) {
        return map.entrySet().stream()
                .map(entry -> {
                    CategoryAmount ca = new CategoryAmount();
                    ca.setName(entry.getKey());
                    ca.setValue(entry.getValue());
                    return ca;
                })
                .collect(Collectors.toList());
    }
}
