package org.example.accounting.service;

import org.example.accounting.DO.Notification;
import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.util.PageResult;

public interface NotificationService {
    PageResult pageQuery(PageQueryDO pageQueryDO);

    void deleteNotifications(String id);

    void updateNotifications(Notification notification);

    void addNotifications(Notification notification);
}
