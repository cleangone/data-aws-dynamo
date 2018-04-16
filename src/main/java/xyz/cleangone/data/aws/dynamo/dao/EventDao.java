package xyz.cleangone.data.aws.dynamo.dao;

import xyz.cleangone.data.aws.dynamo.entity.organization.OrgEvent;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;

import java.util.List;

public class EventDao extends DynamoBaseDao<OrgEvent>
{
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
