package org.example.accounting.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.example.accounting.DAO.NotificationDAO;
import org.example.accounting.DO.Notification;
import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.service.NotificationService;
import org.example.accounting.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationDAO notificationDAO;

    @Override
    public PageResult pageQuery(PageQueryDO pageQueryDO) {
        PageHelper.startPage(pageQueryDO.getPagenum(), pageQueryDO.getPagesize());
        Page<Notification> page = notificationDAO.pageQuery(pageQueryDO);
        return new PageResult(page.getTotal(), page.getPageNum(), page.getResult());
    }

    /**
     * 根据id删除通知
     * @param id
     */
    @Override
    public void deleteNotifications(String id) {
        notificationDAO.deleteNotifications(id);
    }

    /**
     * 根据id修改参数
     * @param notification
     */
    @Override
    public void updateNotifications(Notification notification) {
        notificationDAO.updateNotifications(notification);
    }

    /**
     * 添加
     * @param notification
     */
    @Override
    public void addNotifications(Notification notification) {
        notificationDAO.addNotifications(notification);
    }
}
