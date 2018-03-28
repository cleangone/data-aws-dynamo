package xyz.cleangone.data.aws.dynamo.dao;

import xyz.cleangone.data.aws.dynamo.entity.action.Action;

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
}



