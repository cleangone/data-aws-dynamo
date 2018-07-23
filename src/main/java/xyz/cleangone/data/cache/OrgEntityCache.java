package xyz.cleangone.data.cache;

import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;

import java.util.*;

public class OrgEntityCache<T extends BaseEntity> extends EntityCache<T>
{
    public OrgEntityCache(EntityType entityType)
    {
        super(entityType);
    }
    public OrgEntityCache(EntityType entityType, Integer maxEntities)
    {
        super(entityType, maxEntities);
    }

    public List<T> get(Organization org)
    {
        return get(org.getId(), org.getName(), org.getId());
    }

    public void put(Organization org, List<T> entities, Date start)
    {
        put(org.getId(), entities, start, org.getName(), org.getId());
    }
}
