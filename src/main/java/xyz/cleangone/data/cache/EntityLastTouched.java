package xyz.cleangone.data.cache;

import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.organization.BaseOrg;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityLastTouched
{
    private static EntityLastTouched ENTITY_LAST_TOUCHED;
    public static EntityLastTouched getEntityLastTouched()
    {
        if (ENTITY_LAST_TOUCHED == null) { ENTITY_LAST_TOUCHED = new EntityLastTouched(); }
        return ENTITY_LAST_TOUCHED;
    }

    private final Date baseDate;
    private final Map<String, Date> entityTypeToLastTouched = new HashMap<>();

    private EntityLastTouched()
    {
        baseDate = new Date();
    }

    public void touch(String entityId, EntityType type)
    {
        entityTypeToLastTouched.put(getKey(entityId, type), new Date());
    }

    private String getKey(String entityId, EntityType type)
    {
        return entityId + "-" + type;
    }

    public boolean entityChangedAfter(Date date, BaseEntity entity, EntityType... types) { return entityChangedAfter(date, entity.getId(), types); }

    public boolean entityChangedAfter(Date date, String entityId, EntityType... types)
    {
        for (EntityType type : types)
        {
            if (entityChangedAfter(date, entityId, type)) { return true; }
        }

        return false;
    }

    public boolean entitiesChangedAfter(Date date, List<? extends BaseEntity> entities, EntityType type)
    {
        for (BaseEntity entity : entities)
        {
            if (entityChangedAfter(date, entity, type)) { return true; }
        }

        return false;
    }

    public boolean entityChangedAfter(Date date, BaseEntity entity, EntityType type) { return entityChangedAfter(date, entity.getId(), type); }

    public boolean entityChangedAfter(Date date, String entityId, EntityType type)
    {
        if (date == null) { return true; }

        Date lastTouched = entityTypeToLastTouched.get(getKey(entityId, type));
        if (lastTouched == null) { lastTouched = baseDate; }

        return lastTouched.after(date);
    }
}
