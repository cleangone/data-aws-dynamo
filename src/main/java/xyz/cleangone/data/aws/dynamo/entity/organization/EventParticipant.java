package xyz.cleangone.data.aws.dynamo.entity.organization;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseMixinEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.person.Person;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@DynamoDBTable(tableName="EventParticipant")
public class EventParticipant extends BaseMixinEntity
{
    public static final EntityField LAST_COMMA_FIRST_FIELD = new EntityField("eventParticipant.lastCommaFirst", "Person");
    public static final EntityField SELF_REGISTERED_FIELD = new EntityField("eventParticipant.selfRegistered", "Self Registered");

    private String personId;
    private String eventId;
    private boolean selfRegistered;
    private int count;
    private Person person; // transient

    public EventParticipant() {}
    public EventParticipant(String personId, String eventId)
    {
        setPersonId(requireNonNull(personId));
        setEventId(requireNonNull(eventId));
    }

    @DynamoDBAttribute(attributeName = "PersonId")
    public String getPersonId()
    {
        return personId;
    }
    public void setPersonId(String personId)
    {
        this.personId = personId;
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

    @DynamoDBAttribute(attributeName = "SelfRegistered")
    public boolean getSelfRegistered()
    {
        return selfRegistered;
    }
    public void setSelfRegistered(boolean selfRegistered)
    {
        this.selfRegistered = selfRegistered;
    }

    @DynamoDBAttribute(attributeName = "Count")
    public int getCount()
    {
        return count;
    }
    public void setCount(int count)
    {
        this.count = count;
    }

    @DynamoDBIgnore
    public Person getPerson()
    {
        return person;
    }
    public void setPerson(Person person) { this.person = person; }
    public void setPerson(Person person, Map<String, OrgTag> scopedTagsById)
    {
        this.person = person;
        person.setEventTagsCsv(scopedTagsById);
    }

    @DynamoDBIgnore
    public String getLastCommaFirst()
    {
        return person.getLastCommaFirst();
    }

    @DynamoDBIgnore
    public String getFullName()
    {
        return person.getFirstLast();
    }

    @DynamoDBIgnore
    public String getTagsCsv()
    {
        return person.getEventTagsCsv();
    }
}



