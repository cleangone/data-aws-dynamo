package xyz.cleangone.data.aws.dynamo.dao.event;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.dao.DynamoBaseDao;
import xyz.cleangone.data.aws.dynamo.entity.organization.EventParticipant;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;

import java.util.List;

public class EventParticipantDao extends DynamoBaseDao<EventParticipant>
{
    public List<EventParticipant> getByEvent(String eventId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("EventId", eventId);
        return mapper.scan(EventParticipant.class, scanExpression);
    }

    public void save(EventParticipant participant)
    {
        super.save(participant);
        entityLastTouchedCache.touch(participant.getEventId(), EntityType.Participant);

        // participants are cached by eventId
        setEntityLastTouched(participant.getEventId(), EntityType.Participant);
    }
}



