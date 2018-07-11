package xyz.cleangone.data.aws.dynamo.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityLastTouched;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.cache.EntityLastTouchedCache;

public class CachingDao<T extends BaseEntity> extends DynamoBaseDao<T>
{
    protected EntityLastTouchedCache entityLastTouchedCache = EntityLastTouchedCache.getEntityLastTouchedCache();

    public EntityLastTouched getEntityLastTouched(String entityId)
    {
        // look up the lastTouch by the primary id, which is the entityId
        return mapper.load(EntityLastTouched.class, entityId);
    }

    public void setEntityLastTouched(String entityId, EntityType entityType)
    {
        EntityLastTouched lastTouched = getEntityLastTouched(entityId);
        if (lastTouched == null) { lastTouched = new EntityLastTouched(entityId); }

        lastTouched.setTouchDate(entityType);
        mapper.save(lastTouched);
    }

    protected DynamoDBScanExpression byOrgId(String id)
    {
        return buildEqualsScanExpression("OrgId", id);
    }
    protected DynamoDBScanExpression byEventId(String id)
    {
        return buildEqualsScanExpression("EventId", id);
    }
    protected DynamoDBScanExpression byTag(String tag)
    {
        return buildEqualsScanExpression("Tag", tag);
    }
    protected DynamoDBScanExpression byUserId(String id)
    {
        return buildEqualsScanExpression("UserId", id);
    }
}



