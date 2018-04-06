package xyz.cleangone.data.cache;

import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;

import java.math.BigDecimal;
import java.util.*;

public class EntityCacheStat
{
    private final String entityName;
    private final EntityType entityType;
    private final String orgId;
    private final RetrievalStats hits = new RetrievalStats();
    private final RetrievalStats misses = new RetrievalStats();

    public EntityCacheStat(String entityName, EntityType entityType, String orgId)
    {
        this.entityName = entityName;
        this.entityType = entityType;
        this.orgId = orgId;
    }

    public int getHits()
    {
        return hits.calls;
    }
    public String getAvgHitSeconds()
    {
        return hits.getAvgSeconds().toString();
    }
    public void hit(Date start)
    {
        hits.increment(start);
    }

    public int getMisses()
    {
        return misses.calls;
    }
    public String getAvgMissSeconds()
    {
        return misses.getAvgSeconds().toString();
    }
    public void miss(Date start)
    {
        misses.increment(start);
    }

    public String getEntityName()
    {
        return entityName;
    }
    public String getEntityType()
    {
        return entityType.toString();
    }
    public String getOrgId()
    {
        return orgId;
    }

    class RetrievalStats
    {
        int calls;
        long totalTime;

        void increment(Date start)
        {
            Date now = new Date();
            calls++;
            long callTime = now.getTime() - start.getTime();
            totalTime += callTime;
        }

        BigDecimal getAvgSeconds()
        {
            if (calls == 0) { return BigDecimal.valueOf(0); }

            BigDecimal avgMillis = (new BigDecimal(totalTime)).divide(new BigDecimal(calls), 3, BigDecimal.ROUND_UP);
            BigDecimal avgSecs = avgMillis.divide(new BigDecimal(1000), 3, BigDecimal.ROUND_UP);


            return avgSecs;
        }
    }

}
