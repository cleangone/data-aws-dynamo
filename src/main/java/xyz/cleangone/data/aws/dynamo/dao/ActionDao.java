package xyz.cleangone.data.aws.dynamo.dao;

import xyz.cleangone.data.aws.dynamo.entity.action.Action;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;

import java.util.List;

public class ActionDao extends BaseOrgDao<Action>
{
    public List<Action> getBySourcePersonId(String id)
    {
        return mapper.scan(Action.class, buildEqualsScanExpression("SourcePersonId", id));
    }

    public List<Action> getByTargetEventId(String id)
    {
        return mapper.scan(Action.class, buildEqualsScanExpression("TargetEventId", id));
    }

    public void save(Action action)
    {
        super.save(action);

        if (action.getSourcePersonId() != null)
        {
            entityLastTouchedCache.touch(action.getSourcePersonId(), EntityType.ACTION);
            setEntityLastTouched(action.getSourcePersonId(), EntityType.ACTION);
        }

        if (action.getTargetEventId() != null)
        {
            entityLastTouchedCache.touch(action.getTargetEventId(), EntityType.ACTION);
            setEntityLastTouched(action.getTargetEventId(), EntityType.ACTION);
        }
    }

}



