package xyz.cleangone.data.aws.dynamo.entity.notification;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.item.CatalogItem;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Objects.*;

@DynamoDBTable(tableName = "QueuedNotification")
public class QueuedNotification extends BaseEntity
{
    private static SimpleDateFormat SDF = new SimpleDateFormat("EEE MMM d, hh:mm:ss aaa");

    private String orgId;
    private NotificationType notificationType;
    private Date notificationDate;
    private String itemId;

    public QueuedNotification() { }

    // id is the id of the entity the notification references
    public QueuedNotification(String entityId, String orgId, NotificationType notificationType, Date notificationDate)
    {
        setId(requireNonNull(entityId));
        this.orgId = requireNonNull(orgId);
        this.notificationType = requireNonNull(notificationType);
        this.notificationDate = requireNonNull(notificationDate);
    }

    public QueuedNotification(CatalogItem item, NotificationType notificationType)
    {
       this(item.getId(), item.getOrgId(), notificationType, item.getAvailabilityEnd());
       itemId = item.getId();
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

    public String toFriendlyString()
    {
        return "Notification{id=" + id +
            ", orgId=" + orgId +
            ", notificationDate=" + SDF.format(notificationDate) +
            ", notificationType=" + notificationType.toString() + "}";
    }
}



