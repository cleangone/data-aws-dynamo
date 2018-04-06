package xyz.cleangone.data.aws.dynamo.entity.base;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@DynamoDBTable(tableName = "EntityLastTouched")
public class EntityLastTouched extends BaseEntity
{
    private Map<String, Date> entityTypeToLastTouched = new HashMap<>();

    public EntityLastTouched() { }

    // orgLastTouched.id is the entityId
    public EntityLastTouched(String entityId)
    {
        setId(entityId);
    }

    @DynamoDBIgnore
    public Date getTouchDate(EntityType entityType)
    {
        return getTouchDate(entityType.toString());
    }
    public Date getTouchDate(String entityType)
    {
        return entityTypeToLastTouched.get(entityType);
    }
    public void setTouchDate(EntityType entityType)
    {
        setTouchDate(entityType.toString());
    }
    public void setTouchDate(String entityType)
    {
        entityTypeToLastTouched.put(entityType, new Date());
    }

    @DynamoDBIgnore
    public boolean touchedBefore(EntityType entityType, Date date)
    {
        Date touchDate = getTouchDate(entityType);
        return (touchDate != null && touchDate.before(date));
    }

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.M)
    @DynamoDBAttribute(attributeName = "EntityTypeToLastTouched")
    public Map<String, Date> getEntityTypeToLastTouched()
    {
        return entityTypeToLastTouched;
    }
    public void setEntityTypeToLastTouched(Map<String, Date> entityTypeToLastTouched)
    {
        this.entityTypeToLastTouched = entityTypeToLastTouched;
    }
}



