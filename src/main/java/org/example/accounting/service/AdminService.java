package org.example.accounting.service;

import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.DO.SettingDO;
import org.example.accounting.util.PageResult;

public interface AdminService {

    PageResult pageQuery(PageQueryDO pageQueryDO);

    void updateSettings(SettingDO settingDO);

    SettingDO getSetting();
}
