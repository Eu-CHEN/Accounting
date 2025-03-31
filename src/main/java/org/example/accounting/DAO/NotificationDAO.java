package org.example.accounting.DAO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.example.accounting.DO.Notification;
import org.example.accounting.DO.PageQueryDO;

@Mapper
public interface NotificationDAO {

    Page<Notification> pageQuery(PageQueryDO pageQueryDO);

    void deleteNotifications(String id);

    void updateNotifications(Notification notification);

    /**
     * 添加通知
     * @param notification
     */
    void addNotifications(Notification notification);
}
