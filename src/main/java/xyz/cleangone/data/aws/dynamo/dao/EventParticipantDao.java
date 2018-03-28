package xyz.cleangone.data.aws.dynamo.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.entity.organization.EventParticipant;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;
import xyz.cleangone.data.cache.EntityLastTouched;
import xyz.cleangone.data.cache.EntityType;

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
        entityLastTouched.touch(participant.getEventId(), EntityType.Participant);
    }
}



