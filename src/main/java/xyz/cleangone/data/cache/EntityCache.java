package xyz.cleangone.data.cache;

import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseNamedEntity;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityLastTouched;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.aws.dynamo.dao.org.OrgDao;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;

import java.util.*;
import java.util.stream.Collectors;

// cache of a particular kind of entity by an entityId (usually orgId, can be eventId, personId)
public class EntityCache <T extends BaseEntity>
{
    private static final OrgDao DAO = new OrgDao();

    private final Map<String, EntityCacheItem<T>> entityIdToEntityCacheItems = new HashMap<>();
    private final Map<String, Date> entityIdLastChecked = new HashMap<>();
    private final List<String> entityIds = new ArrayList<>();
    private final Map<String, EntityCacheStat> entityIdToStat = new HashMap<>();

    private final EntityType entityType;
    private final Integer maxEntities;

    public EntityCache(EntityType entityType)
    {
        this(entityType, null);
    }
    public EntityCache(EntityType entityType, Integer maxEntities)
    {
        this.entityType = entityType;
        this.maxEntities = maxEntities;
    }

    public void clear(BaseNamedEntity keyEntity) { clear(keyEntity.getId()); }
    public void clear(String keyEntityId)
    {
        entityIdToEntityCacheItems.put(keyEntityId, null);
        entityIdLastChecked.put(keyEntityId, null);
        entityIds.remove(keyEntityId);
    }

    public List<T> get(Organization org)
    {
        return get(org, org.getId());
    }
    public List<T> get(BaseNamedEntity keyEntity, String orgId)
    {
        Date start = new Date();
        EntityCacheItem <T> entityCacheItem = entityIdToEntityCacheItems.get(keyEntity.getId());
        if (entityCacheItem == null)
        {
            return null;
        }

        Date lastChecked = entityIdLastChecked.get(keyEntity.getId());
        if (lastChecked != null && lastChecked.getTime() + 5000 > start.getTime())
        {
            // checked lastTouch within last 5 secs
            hit(keyEntity, orgId, start);
            return entityCacheItem.getEntities();
        }

        EntityLastTouched lastTouch = DAO.getEntityLastTouched(keyEntity.getId());
        entityIdLastChecked.put(keyEntity.getId(), start);
        if (lastTouch != null &&
            lastTouch.touchedBefore(entityType, entityCacheItem.getCacheDate()))
        {
            hit(keyEntity, orgId, start);
            return entityCacheItem.getEntities();
        }

        entityIdToEntityCacheItems.remove(keyEntity.getId());
        return null;
    }

    public void put(Organization org, List<T> entities, Date start)
    {
        put(org, entities, org.getId(), start);
    }
    public void put(BaseNamedEntity keyEntity, List<T> entities, String orgId, Date start)
    {
        entityIdToEntityCacheItems.put(keyEntity.getId(), new EntityCacheItem<>(entities));
        miss(keyEntity, orgId, start);

        if (maxEntities != null)
        {
            // todo - make this a more applicable collection, like a stack
            // beginning of list is most recent
            entityIds.remove(keyEntity.getId());
            entityIds.add(0, keyEntity.getId());

            if (entityIds.size() > maxEntities)
            {
                String entityIdToRemove = entityIds.remove(entityIds.size() - 1);
                entityIdToEntityCacheItems.remove(entityIdToRemove);
            }
        }
    }

    public List<EntityCacheStat> getCacheStats(String orgId)
    {
        return entityIdToStat.values().stream()
            .filter(s -> s.getOrgId().equals(orgId))
            .collect(Collectors.toList());
    }

    private void hit(BaseNamedEntity keyEntity, String orgId, Date start)
    {
        getStat(keyEntity, orgId).hit(start);
    }
    private void miss(BaseNamedEntity keyEntity, String orgId, Date start)
    {
        getStat(keyEntity, orgId).miss(start);
    }

    private EntityCacheStat getStat(BaseNamedEntity keyEntity, String orgId)
    {
        EntityCacheStat stat = entityIdToStat.get(keyEntity.getId());
        if (stat == null)
        {
            stat = new EntityCacheStat(keyEntity.getName(), entityType, orgId);
            entityIdToStat.put(keyEntity.getId(), stat);
        }

        return stat;
    }

    class EntityCacheItem <T extends BaseEntity>
    {
        private Date cacheDate;
        private List<T> entities;

        EntityCacheItem(List<T> entities)
        {
            cacheDate = new Date();
            this.entities = entities;
        }

        Date getCacheDate()
        {
            return cacheDate;
        }
        List<T> getEntities()
        {
            return entities;
        }
    }



}
