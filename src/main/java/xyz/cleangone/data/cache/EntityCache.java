package xyz.cleangone.data.cache;

import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseMixinEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityLastTouched;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;
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

    public List<T> get(Organization org)
    {
        return get(org, org.getId());
    }
    public List<T> get(BaseMixinEntity entity, String orgId)
    {
        Date start = new Date();
        EntityCacheItem <T> entityCacheItem = entityIdToEntityCacheItems.get(entity.getId());
        if (entityCacheItem == null)
        {
            return null;
        }

        Date lastChecked = entityIdLastChecked.get(entity.getId());
        if (lastChecked != null && lastChecked.getTime() + 5000 > start.getTime())
        {
            // checked lastTouch within last 5 secs
            hit(entity, orgId, start);
            return entityCacheItem.getEntities();
        }

        EntityLastTouched lastTouch = DAO.getEntityLastTouched(entity.getId());
        entityIdLastChecked.put(entity.getId(), start);
        if (lastTouch != null &&
            lastTouch.touchedBefore(entityType, entityCacheItem.getCacheDate()))
        {
            hit(entity, orgId, start);
            return entityCacheItem.getEntities();
        }

        entityIdToEntityCacheItems.remove(entity.getId());
        return null;
    }

    public void put(Organization org, List<T> entities, Date start)
    {
        put(org, entities, org.getId(), start);
    }
    public void put(BaseMixinEntity entity, List<T> entities, String orgId, Date start)
    {
        entityIdToEntityCacheItems.put(entity.getId(), new EntityCacheItem<>(entities));
        miss(entity, orgId, start);

        if (maxEntities != null)
        {
            // todo - make this a more applicable collection, like a stack
            // beginning of list is most recent
            entityIds.remove(entity.getId());
            entityIds.add(0, entity.getId());

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

    private void hit(BaseMixinEntity entity, String orgId, Date start)
    {
        getStat(entity, orgId).hit(start);
    }
    private void miss(BaseMixinEntity entity, String orgId, Date start)
    {
        getStat(entity, orgId).miss(start);
    }

    private EntityCacheStat getStat(BaseMixinEntity entity, String orgId)
    {
        EntityCacheStat stat = entityIdToStat.get(entity.getId());
        if (stat == null)
        {
            stat = new EntityCacheStat(entity.getName(), entityType, orgId);
            entityIdToStat.put(entity.getId(), stat);
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
