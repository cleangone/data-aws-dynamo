package xyz.cleangone.data.aws.dynamo.entity.person;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.Objects;

@DynamoDBDocument
public class AdminPrivledge
{
    private String orgId;
    private String eventId;

    public AdminPrivledge() {}
    public AdminPrivledge(String orgId)
    {
        this.orgId = orgId;
    }
    public AdminPrivledge(String orgId, String eventId)
    {
        this.orgId = orgId;
        this.eventId = eventId;
    }

    @DynamoDBIgnore public boolean isSuperAdmin()
    {
        return (orgId == null && eventId == null);
    }
    @DynamoDBIgnore public boolean isOrgAdmin(String orgId)
    {
        return (isOrg(orgId) && eventId == null);
    }
    @DynamoDBIgnore public boolean isEventAdmin(String orgId)
    {
        return (isOrg(orgId) && eventId != null);
    }
    @DynamoDBIgnore public boolean isEventAdmin(String orgId, String eventId)
    {
        return (isOrg(orgId) && isEvent(eventId));
    }

    @DynamoDBIgnore public boolean isOrg(String orgId)
    {
        return Objects.requireNonNull(orgId).equals(this.orgId);
    }
    @DynamoDBIgnore public boolean isEvent(String eventId)
    {
        return Objects.requireNonNull(eventId).equals(this.eventId);
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof AdminPrivledge)) return false;

        AdminPrivledge that = (AdminPrivledge) o;

        if (getOrgId() != null ? !getOrgId().equals(that.getOrgId()) : that.getOrgId() != null) return false;
        return getEventId() != null ? getEventId().equals(that.getEventId()) : that.getEventId() == null;
    }

    @Override
    public int hashCode()
    {
        int result = getOrgId() != null ? getOrgId().hashCode() : 0;
        result = 31 * result + (getEventId() != null ? getEventId().hashCode() : 0);
        return result;
    }
}
