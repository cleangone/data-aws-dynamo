package xyz.cleangone.data.aws.dynamo.dao;

import xyz.cleangone.data.aws.dynamo.entity.notification.QueuedNotification;

import java.util.List;

public class QueuedNotificationDao extends DynamoBaseDao<QueuedNotification>
{
    public QueuedNotification getById(String id)
    {
        return mapper.load(QueuedNotification.class, id);
    }

    public List<QueuedNotification> getByOrg(String orgId)
    {
        return mapper.scan(QueuedNotification.class, byOrgId(orgId));
    }

    public void delete(QueuedNotification notification)
    {
        mapper.delete(notification);
    }
}



