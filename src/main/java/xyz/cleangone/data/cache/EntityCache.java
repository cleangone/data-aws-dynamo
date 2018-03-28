package xyz.cleangone.data.cache;

import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.OrgLastTouched;
import xyz.cleangone.data.aws.dynamo.dao.OrgDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// cache of a particular kind of entity by orgId
public class EntityCache <T extends BaseEntity>
{
    private static final OrgDao DAO = new OrgDao();
    private final Map<String, EntityCacheItem<T>> entityCacheItems = new HashMap<>();

    private int cacheHits = 0;
    private int cacheMisses = 0;

    public List<T> get(String orgId)
    {
        EntityCacheItem <T> entityCacheItem = entityCacheItems.get(orgId);
        if (entityCacheItem == null)
        {
            cacheMisses++;
            return null;
        }

        OrgLastTouched currentLastTouch = DAO.getLastTouch(orgId);
        if (currentLastTouch != null && currentLastTouch.equals(entityCacheItem.getLastTouch()))
        {
            cacheHits++;
            return entityCacheItem.getEntities();
        }

        cacheMisses++;
        entityCacheItems.remove(orgId);
        return null;
    }

    public void set(List<T> entities, String orgId)
    {
        OrgLastTouched lastTouch = DAO.getLastTouch(orgId);
        entityCacheItems.put(orgId, new EntityCacheItem<>(lastTouch, entities));
    }

    class EntityCacheItem <T extends BaseEntity>
    {
        private OrgLastTouched lastTouch;
        private List<T> entities;

        EntityCacheItem(OrgLastTouched lastTouch, List<T> entities)
        {
            this.lastTouch = lastTouch;
            this.entities = entities;
        }

        OrgLastTouched getLastTouch()
        {
            return lastTouch;
        }
        List<T> getEntities()
        {
            return entities;
        }
    }
}
