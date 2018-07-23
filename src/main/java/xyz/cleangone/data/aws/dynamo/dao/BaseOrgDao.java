package xyz.cleangone.data.aws.dynamo.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityLastTouched;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.cache.EntityLastTouchedCache;

public class BaseOrgDao<T extends BaseEntity> extends CachingDao<T>
{
    // todo - is this still used?  why?
    protected EntityLastTouchedCache entityLastTouchedCache = EntityLastTouchedCache.getEntityLastTouchedCache();

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



