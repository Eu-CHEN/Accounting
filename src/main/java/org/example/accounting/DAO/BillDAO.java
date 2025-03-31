package org.example.accounting.DAO;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.accounting.DO.BillDO;
import org.example.accounting.DO.BillTypes;
import org.example.accounting.DO.PageQueryDO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface BillDAO {

    //分页查询
    Page<BillDO> pageQuery(PageQueryDO pageQueryDO);

    //添加账单
    void addBill(BillDO billDO);

    //删除账单
    void deleteBill(Integer id);

    /**
     * 根据id修改账单
     */
    void updateBill(BillDO billDO);

    /**
     * 根据用户名查询账单
     * @param username
     * @return
     */
    @Select("select * from bill where username = #{username}")
    List<BillDO> getBillByUsername(String username);


    List<BillDO> getBillsByUsernameAndTimeRange(
            @Param("username") String username,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
