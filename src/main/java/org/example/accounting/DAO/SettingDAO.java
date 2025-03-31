package org.example.accounting.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.example.accounting.DO.SettingDO;

@Mapper
public interface SettingDAO {

    /**
     * 修改系统设置
     * @param settingDO
     */
    void updateSettings(SettingDO settingDO);

    /**
     * 获取系统设置
     * @return
     */
    SettingDO getSetting();
}
