package xyz.cleangone.data.aws.dynamo.entity.person;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;

import java.util.*;
import java.util.stream.Collectors;

@DynamoDBTable(tableName="Person")
public class Person extends BaseEntity
{
    public static final EntityField FIRST_NAME_FIELD = new EntityField("person.firstName", "First Name");
    public static final EntityField LAST_NAME_FIELD = new EntityField("person.lastName", "Last Name");
    public static final EntityField TAGS_FIELD = new EntityField("person.tags", "Tags");

    private String orgId;
    private String firstName;
    private String lastName;
    private List<String> tagIds;
    private String tagsCsv; // transient
    private String eventTagsCsv; // transient, sep fm tagsCsv to allow display in People and Participants

    public Person() { }

    public Person(String orgId, String firstName, String lastName)
    {
        this.orgId = orgId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @DynamoDBIgnore
    public String getLastCommaFirst()
    {
        return lastName + ", " + firstName;
    }

    @DynamoDBIgnore
    public String getFirstLast() { return firstName + " " +  lastName; }

    public String get(EntityField field)
    {
        if (FIRST_NAME_FIELD.equals(field)) return getFirstName();
        else if (LAST_NAME_FIELD.equals(field)) return getLastName();
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (FIRST_NAME_FIELD.equals(field)) setFirstName(value);
        else if (LAST_NAME_FIELD.equals(field)) setLastName(value);
        else super.set(field, value);
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

    @DynamoDBAttribute(attributeName="FirstName")
    public String getFirstName() { return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}

    @DynamoDBAttribute(attributeName="LastName")
    public String getLastName() { return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}

    @DynamoDBAttribute(attributeName="TagIds")
    public List<String> getTagIds()
    {
        if (tagIds == null) { tagIds = new ArrayList<>(); }
        return tagIds;
    }
    public void setTagIds(List<String> tagIds)
    {
        this.tagIds = tagIds;
    }
    public void addTagId(String tagId)
    {
        if (!getTagIds().contains(tagId)) { tagIds.add(tagId); }
    }
    public void removeTagId(String tagId)
    {
        tagIds.remove(tagId);
    }
    public boolean includesOneOfTags(Set<String> tagIds)
    {
        for (String myTagId : getTagIds())
        {
            if (tagIds.contains(myTagId)) { return true; }
        }

        return false;
    }

    @DynamoDBIgnore
    public String getTagsCsv() { return tagsCsv; }
    public void setTagsCsv(Map<String, OrgTag> tagsById)
    {
        tagsCsv = getCsv(tagsById);
    }

    @DynamoDBIgnore
    public String getEventTagsCsv() { return eventTagsCsv; }
    public void setEventTagsCsv(Map<String, OrgTag> tagsById)
    {
        eventTagsCsv = getCsv(tagsById);
    }

    private String getCsv(Map<String, OrgTag> tagsById)
    {
        if (getTagIds().isEmpty()) { return ""; }

        List<String> tagNames = tagIds.stream()
            .filter(id -> tagsById.containsKey(id)) // map may be of a subset of tags
            .map(id -> tagsById.get(id))
            .map(OrgTag::getName)
            .collect(Collectors.toList());

        Collections.sort(tagNames);
        return tagNames.stream().collect(Collectors.joining(", "));
    }
}



