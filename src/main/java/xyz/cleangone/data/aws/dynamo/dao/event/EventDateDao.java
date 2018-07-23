package xyz.cleangone.data.aws.dynamo.dao.event;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.dao.BaseOrgDao;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.organization.EventDate;

import java.util.List;

public class EventDateDao extends BaseOrgDao<EventDate>
{
    public List<EventDate> getByOrg(String orgId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("OrgId", orgId);
        return mapper.scan(EventDate.class, scanExpression);
    }

    public List<EventDate> getByEvent(String eventId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("EventId", eventId);
        return mapper.scan(EventDate.class, scanExpression);
    }

    public void save(EventDate eventDate)
    {
        super.save(eventDate);
        setEntityLastTouched(eventDate.getOrgId(), EntityType.EVENTDATE);
    }
}



