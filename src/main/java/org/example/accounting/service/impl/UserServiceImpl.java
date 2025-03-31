package org.example.accounting.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.example.accounting.DAO.UserDAO;
import org.example.accounting.DO.BillDO;
import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.DO.UserDO;
import org.example.accounting.model.User;
import org.example.accounting.service.UserService;
import org.example.accounting.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;


    @Override
    public User login(UserDO userDO) {
        List<UserDO> userList = userDAO.getAllUser();
        User user1 = new User();
        for (UserDO user : userList) {
            if (user.getUsername().equals(userDO.getUsername()) && user.getPassword().equals(userDO.getPassword())) {
                user1 = user.toModel(user);
                break;
            }else {
                user1 = null;
            }
        }
        return user1;
    }

    @Override
    public User addUser(UserDO userDO) {
        userDO.setAddTime(LocalDateTime.now());
        userDO.setUsertype("user");
        userDAO.addUser(userDO);
        User user = userDO.toModel(userDO);
        return user;
    }

    @Override
    public void updateUser(UserDO userDO) {
        userDAO.UpdateUser(userDO);
    }

    @Override
    public PageResult pageQuery(PageQueryDO pageQueryDO) {
        PageHelper.startPage(pageQueryDO.getPagenum(), pageQueryDO.getPagesize());
        Page<UserDO> page = userDAO.pageQuery(pageQueryDO);
        return new PageResult(page.getTotal(), page.getPageNum(), page.getResult());
    }

    @Override
    public void deleteUser(UserDO userDO) {
        userDAO.deleteUser(userDO.getId());
    }
}
