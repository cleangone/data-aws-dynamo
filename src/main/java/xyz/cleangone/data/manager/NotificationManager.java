package xyz.cleangone.data.manager;

import xyz.cleangone.data.aws.dynamo.dao.*;
import xyz.cleangone.data.aws.dynamo.entity.notification.QueuedNotification;
import xyz.cleangone.data.aws.dynamo.entity.organization.*;

import java.util.*;

public class NotificationManager
{
    private final String orgId;
    private final QueuedNotificationDao queuedNotificationDao = new QueuedNotificationDao();

    public NotificationManager(String orgId)
    {
        this.orgId = orgId;
    }

    public QueuedNotification getNotification(String id)
    {
        return queuedNotificationDao.getById(id);
    }

    public List<QueuedNotification> getNotifications()
    {
        List<QueuedNotification> notifications = new ArrayList<>(queuedNotificationDao.getByOrg(orgId));
        notifications.sort((n1, n2) -> n1.getNotificationDate().before(n2.getNotificationDate()) ? -1 : 1);

        return notifications;
    }

    public QueuedNotification getEarliestNotification()
    {
        List<QueuedNotification> notifications = getNotifications();
        return notifications.isEmpty() ? null : notifications.get(0);
    }

    public void save(QueuedNotification notification)
    {
        queuedNotificationDao.save(notification);
    }
    public void delete(QueuedNotification notification)
    {
        queuedNotificationDao.delete(notification);
    }

    public String getOrgId()
    {
        return orgId;
    }
}
