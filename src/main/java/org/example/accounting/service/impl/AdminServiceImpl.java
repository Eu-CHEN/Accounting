package org.example.accounting.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.example.accounting.DAO.SettingDAO;
import org.example.accounting.DAO.UserDAO;
import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.DO.SettingDO;
import org.example.accounting.DO.UserDO;
import org.example.accounting.service.AdminService;
import org.example.accounting.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private SettingDAO settingDAO;

    @Override
    public PageResult pageQuery(PageQueryDO pageQueryDO) {
        PageHelper.startPage(pageQueryDO.getPagenum(), pageQueryDO.getPagesize());
        Page<UserDO> page = userDAO.pageQuery(pageQueryDO);
        return new PageResult(page.getTotal(), page.getPageNum(), page.getResult());
    }

    @Override
    public void updateSettings(SettingDO settingDO) {
        settingDAO.updateSettings(settingDO);
    }

    @Override
    public SettingDO getSetting() {
        SettingDO settingDO = settingDAO.getSetting();
        return settingDO;
    }
}
