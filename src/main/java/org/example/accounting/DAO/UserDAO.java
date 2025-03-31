package org.example.accounting.DAO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.accounting.DO.BillDO;
import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.DO.UserDO;

import java.util.List;


@Mapper
public interface UserDAO {
    /**
     * 找出所有的用户
     * @return
     */
    List<UserDO> getAllUser();

    /**
     * 添加新用户
     */
    int addUser(UserDO user);

    /**
     * 根据id修改用户数据
     */
    void UpdateUser(UserDO userDO);

    Page<UserDO> pageQuery(PageQueryDO pageQueryDO);

    /**
     * 根据id删除用户
     * @param id
     */
    void deleteUser(String id);
}
