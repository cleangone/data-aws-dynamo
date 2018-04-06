package xyz.cleangone.data.cache;

import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityLastTouched;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityLastTouchedCache
{
    private static EntityLastTouchedCache LAST_TOUCHED;
    public static EntityLastTouchedCache getEntityLastTouchedCache()
    {
        if (LAST_TOUCHED == null) { LAST_TOUCHED = new EntityLastTouchedCache(); }
        return LAST_TOUCHED;
    }

    private final Date baseDate;
    private final Map<String, EntityLastTouched> entityIdToLastTouched = new HashMap<>();

    private EntityLastTouchedCache()
    {
        baseDate = new Date();
    }

    public void touch(String entityId, EntityType entityType)
    {
        EntityLastTouched lastTouched = entityIdToLastTouched.get(entityId);
        if (lastTouched == null)
        {
            lastTouched = new EntityLastTouched(entityId);
            entityIdToLastTouched.put(entityId, lastTouched);
        }

        lastTouched.setTouchDate(entityType);
    }

    public boolean entitiesChangedAfter(Date date, List<? extends BaseEntity> entities, EntityType type)
    {
        for (BaseEntity entity : entities)
        {
            if (entityChangedAfter(date, entity, type)) { return true; }
        }

        return false;
    }

    public boolean entityChangedAfter(Date date, BaseEntity entity, EntityType... entityTypes) { return entityChangedAfter(date, entity.getId(), entityTypes); }
    public boolean entityChangedAfter(Date date, String entityId,   EntityType... entityTypes)
    {
        for (EntityType entityType : entityTypes)
        {
            if (entityChangedAfter(date, entityId, entityType)) { return true; }
        }

        return false;
    }

    public boolean entityChangedAfter(Date date, BaseEntity entity, EntityType entityType) { return entityChangedAfter(date, entity.getId(), entityType); }
    public boolean entityChangedAfter(Date date, String entityId,   EntityType entityType)
    {
        if (date == null) { return true; }

        EntityLastTouched lastTouched = entityIdToLastTouched.get(entityId);
        Date touchDate = lastTouched == null ? null : lastTouched.getTouchDate(entityType);
        touchDate = touchDate == null ? baseDate : touchDate;

        return touchDate.after(date);
    }
}
