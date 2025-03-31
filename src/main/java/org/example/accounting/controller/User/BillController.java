package org.example.accounting.controller.User;

import org.example.accounting.DO.BillDO;
import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.model.Bill;
import org.example.accounting.model.BillCategoryStats;
import org.example.accounting.model.BillTrendVO;
import org.example.accounting.service.BillService;
import org.example.accounting.util.PageResult;
import org.example.accounting.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/login/bill")
@CrossOrigin
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping("/findbill")
    public Result<PageResult> getAllBill(PageQueryDO pageQueryDO){
        PageResult pageresult = billService.pageQuery(pageQueryDO);
        Result<PageResult> result = new Result<>();
        result.setData(pageresult);
        result.setMessage("查询成功");
        result.setCode(200);
        return result;
    }

    @PostMapping("/addbill")
    public Result<Bill> addBill(@RequestBody BillDO billDO){
        Result<Bill> result = new Result<>();
        billService.addBill(billDO);
        result.setCode(200);
        result.setMessage("订单添加成功");
        result.setData(billDO.toModel(billDO));
        return result;
    }

    /**
     * 根据id修改账单
     * @param billDO
     * @return
     */
    @PostMapping("/update")
    public Result updateBill(@RequestBody BillDO billDO){
        Result result = new Result<>();
        billService.updateBill(billDO);
        result.setCode(200);
        result.setMessage("修改成功");
        result.setData("修改成功");
        return result;
    }

    @PostMapping("/delete")
    public Result deleteBill(@RequestBody BillDO billDO){
        billService.deleteBill(billDO.getId());
        Result result = new Result();
        result.setCode(200);
        result.setMessage("删除成功");
        result.setData("删除成功");

        return result;
    }

    @GetMapping("/categories")
    public Result getTypes(){
        Result result = new Result();
        List<String> list = billService.getTypes();
        result.setCode(200);
        result.setData(list);
        result.setMessage("类别查询成功");

        return result;
    }

    @GetMapping("/types")
    public Result getCategories(){
        Result result = new Result();
        List<String> list = billService.getCategories();
        result.setCode(200);
        result.setData(list);
        result.setMessage("类别支付方式成功");

        return result;
    }


    @GetMapping("/category-stats")
    public Result<BillCategoryStats> getCategoryStats(String username, String timeRange) {
        Result<BillCategoryStats> result = new Result<>();
        try {
            BillCategoryStats stats = billService.getCategoryStats(username, timeRange);
            result.setCode(200);
            result.setMessage("获取分类统计成功");
            result.setData(stats);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取分类统计失败");
        }
        return result;
    }

    @GetMapping("/trend")
    public Result<BillTrendVO> getTrend(String username, String timeRange) {
        Result<BillTrendVO> result = new Result<>();
        try {
            BillTrendVO trend = billService.getTrend(username, timeRange);
            result.setCode(200);
            result.setMessage("获取收支趋势成功");
            result.setData(trend);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取收支趋势失败");
        }
        return result;
    }
}
