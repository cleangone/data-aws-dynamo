package xyz.cleangone.data.aws.dynamo.entity.base;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Date;

@DynamoDBTable(tableName = "OrgLastTouched")
public class OrgLastTouched extends BaseEntity
{

// todo - change to EntityLastTouched
    public OrgLastTouched() { }

    protected Date entityUpdated;
    protected Date eventsUpdated;
    protected Date peopleUpdated;
    protected Date usersUpdated;
    protected Date actionsUpdated;

    // orgLastTouched.id is the orgId
    public OrgLastTouched(String orgId)
    {
        setId(orgId);
    }

    public Date getTouchDate(EntityType entityType)
    {
        if (entityType == EntityType.Entity)      { return getEntityUpdated(); }
        else if (entityType == EntityType.Event)  { return getEventsUpdated(); }
        else if (entityType == EntityType.Person) { return getPeopleUpdated(); }
        else if (entityType == EntityType.User)   { return getUsersUpdated(); }
        else if (entityType == EntityType.Action) { return getActionsUpdated(); }
        else return null;
    }

    public void setTouchDate(EntityType entityType)
    {
        if (entityType == EntityType.Entity)      { setEntityUpdated(new Date()); }
        else if (entityType == EntityType.Event)  { setEventsUpdated(new Date()); }
        else if (entityType == EntityType.Person) { setPeopleUpdated(new Date()); }
        else if (entityType == EntityType.User)   { setUsersUpdated(new Date()); }
        else if (entityType == EntityType.Action) { setActionsUpdated(new Date()); }
    }

    @DynamoDBAttribute(attributeName="EntityUpdated")
    public Date getEntityUpdated()
    {
        return entityUpdated;
    }
    public void setEntityUpdated(Date entityUpdated)
    {
        this.entityUpdated = entityUpdated;
    }

    @DynamoDBAttribute(attributeName="EventsUpdated")
    public Date getEventsUpdated()
    {
        return eventsUpdated;
    }
    public void setEventsUpdated(Date eventsUpdated)
    {
        this.eventsUpdated = eventsUpdated;
    }

    @DynamoDBAttribute(attributeName="PeopleUpdated")
    public Date getPeopleUpdated()
    {
        return peopleUpdated;
    }
    public void setPeopleUpdated(Date peopleUpdated)
    {
        this.peopleUpdated = peopleUpdated;
    }

    @DynamoDBAttribute(attributeName="UsersUpdated")
    public Date getUsersUpdated()
    {
        return usersUpdated;
    }
    public void setUsersUpdated(Date usersUpdated)
    {
        this.usersUpdated = usersUpdated;
    }

    @DynamoDBAttribute(attributeName="ActionsUpdated")
    public Date getActionsUpdated()
    {
        return actionsUpdated;
    }
    public void setActionsUpdated(Date actionsUpdated)
    {
        this.actionsUpdated = actionsUpdated;
    }
}



