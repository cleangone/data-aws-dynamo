package xyz.cleangone.data.aws.dynamo.dao.event;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.dao.BaseOrgDao;
import xyz.cleangone.data.aws.dynamo.entity.organization.EventParticipant;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;

import java.util.List;

public class EventParticipantDao extends BaseOrgDao<EventParticipant>
{
    public List<EventParticipant> getByEvent(String eventId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("EventId", eventId);
        return mapper.scan(EventParticipant.class, scanExpression);
    }

    public void save(EventParticipant participant)
    {
        entityLastTouchedCache.touch(participant.getEventId(), EntityType.PARTICIPANT);

        // participants are cached by eventId
        setEntityLastTouched(participant.getEventId(), EntityType.PARTICIPANT);

        super.save(participant);
    }

    public void delete(EventParticipant participant)
    {
        entityLastTouchedCache.touch(participant.getEventId(), EntityType.PARTICIPANT);
        setEntityLastTouched(participant.getEventId(), EntityType.PARTICIPANT);

        super.delete(participant);
    }
}



