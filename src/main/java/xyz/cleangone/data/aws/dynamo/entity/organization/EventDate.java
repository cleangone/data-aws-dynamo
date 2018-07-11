package xyz.cleangone.data.aws.dynamo.entity.organization;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseNamedEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;

import java.time.*;
import java.util.Date;

import static java.util.Objects.requireNonNull;

@DynamoDBTable(tableName="EventDate")
public class EventDate extends BaseNamedEntity
{
    public static final EntityField DATE_NAME_FIELD = new EntityField(NAME_FIELD, "Date Name");

    public static final EntityField DATE_FIELD = new EntityField("eventDate.date", "Date");
    public static final EntityField DETAILS_FIELD = new EntityField("eventDate.details", "Details");

    private String orgId;
    private String eventId;
    private Date dateTime;
    private String details;

    public EventDate() {}
    public EventDate(String orgId, String eventId, String name)
    {
        super(requireNonNull(name));
        setOrgId(requireNonNull(orgId));
        setEventId(requireNonNull(eventId));
    }

    @DynamoDBAttribute(attributeName = "OrgId")
    public String getOrgId()
    {
        return orgId;
    }
    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    @DynamoDBAttribute(attributeName = "EventId")
    public String getEventId()
    {
        return eventId;
    }
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    @DynamoDBAttribute(attributeName = "DateTime")
    public Date getDateTime()
    {
        return dateTime;
    }
    public void setDateTime(Date dateTime)
    {
        this.dateTime = dateTime;
    }

    @DynamoDBAttribute(attributeName = "Details")
    public String getDetails()
    {
        return details;
    }
    public void setDetails(String details)
    {
        this.details = details;
    }

    @DynamoDBIgnore
    public LocalDateTime getLocalDateTime()
    {
        if (dateTime == null) { return null; }

        Instant instant = Instant.ofEpochMilli(dateTime.getTime());
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public void setLocalDateTime(LocalDateTime ldt)
    {
        if (ldt == null)
        {
            dateTime = null;
            return;
        }

        Instant instant = ldt.toInstant(ZoneOffset.UTC);
        dateTime = Date.from(instant);
    }

    @DynamoDBIgnore
    public ZonedDateTime getZoneDateTime()
    {
        LocalDateTime ldt = getLocalDateTime();
        ZoneId zoneId = ZoneId.systemDefault();
        return ldt.atZone(zoneId);
    }
}



