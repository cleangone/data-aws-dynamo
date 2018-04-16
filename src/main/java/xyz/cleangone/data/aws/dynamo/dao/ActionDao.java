package xyz.cleangone.data.aws.dynamo.dao;

import xyz.cleangone.data.aws.dynamo.entity.action.Action;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;

import java.util.List;

public class ActionDao extends DynamoBaseDao<Action>
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
            entityLastTouchedCache.touch(action.getSourcePersonId(), EntityType.Action);
            setEntityLastTouched(action.getSourcePersonId(), EntityType.Action);
        }

        if (action.getTargetEventId() != null)
        {
            entityLastTouchedCache.touch(action.getTargetEventId(), EntityType.Action);
            setEntityLastTouched(action.getTargetEventId(), EntityType.Action);
        }
    }

}



