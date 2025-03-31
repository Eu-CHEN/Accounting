package org.example.accounting.service;

import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.DO.UserDO;
import org.example.accounting.model.User;
import org.example.accounting.util.PageResult;

import java.util.List;

public interface UserService {

    User login(UserDO userDO);

    User addUser(UserDO userDO);

    void updateUser(UserDO userDO);

    PageResult pageQuery(PageQueryDO pageQueryDO);

    void deleteUser(UserDO userDO);
}
