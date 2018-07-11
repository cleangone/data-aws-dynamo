package xyz.cleangone.data.aws.dynamo.dao.event;

import xyz.cleangone.data.aws.dynamo.dao.CachingDao;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgEvent;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;

import java.util.List;

public class EventDao extends CachingDao<OrgEvent>
{
    public OrgEvent getById(String id)
    {
        return mapper.load(OrgEvent.class, id);
    }

    public List<OrgEvent> getByOrg(String orgId)
    {
        return mapper.scan(OrgEvent.class, byOrgId(orgId));
    }

    public void save(OrgEvent event)
    {
        super.save(event);
        entityLastTouchedCache.touch(event.getId(), EntityType.Entity);
        setEntityLastTouched(event.getOrgId(), EntityType.Event);
    }
}
