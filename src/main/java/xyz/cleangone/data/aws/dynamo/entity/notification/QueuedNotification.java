package xyz.cleangone.data.aws.dynamo.entity.notification;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;

import java.util.Date;

@DynamoDBTable(tableName = "QueuedNotification")
public class QueuedNotification extends BaseEntity
{
    private String orgId;
    private NotificationType notificationType;
    private Date notificationDate;
    private String itemId;

    public QueuedNotification() { }

    // QueuedNotification.id is the id of the entity the notification references
    public QueuedNotification(
        String entityId, String orgId, NotificationType notificationType, Date notificationDate)
    {
        setId(entityId);
        this.orgId = orgId;
        this.notificationType = notificationType;
        this.notificationDate = notificationDate;
    }

    @DynamoDBAttribute(attributeName = "OrgId")
    public String getOrgId()
    {
        return orgId;
    }
    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    public NotificationType getNotificationType()
    {
        return notificationType;
    }
    public void setNotificationType(NotificationType notificationType)
    {
        this.notificationType = notificationType;
    }

    @DynamoDBAttribute(attributeName = "NotificationDate")
    public Date getNotificationDate()
    {
        return notificationDate;
    }
    public void setNotificationDate(Date notificationDate)
    {
        this.notificationDate = notificationDate;
    }

    @DynamoDBAttribute(attributeName = "ItemId")
    public String getItemId()
    {
        return itemId;
    }
    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }
}



