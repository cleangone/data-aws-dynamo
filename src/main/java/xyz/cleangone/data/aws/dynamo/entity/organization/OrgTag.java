package xyz.cleangone.data.aws.dynamo.entity.organization;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseMixinEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseNamedEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.person.Person;

import java.util.Comparator;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@DynamoDBTable(tableName="OrgTag")
public class OrgTag extends BaseNamedEntity implements Comparable<OrgTag>
{
    public static final EntityField EVENT_NAME_FIELD = new EntityField("tag.eventName", "Event Name");

    public static String USER = "user";

    // todo - add a list of permanent tag names, i.e. Notifications, EVENT_ADMIN ?


    public static final String ORG_ID_ATTRIBUTE = "OrgId";
    public static final EntityField TAG_NAME_FIELD = new EntityField(NAME_FIELD, "Tag Name");
    public static final EntityField DISPLAY_ORDER_FIELD = new EntityField("orgTag.displayOrder", "Display Order");
    public static final EntityField USER_VISIBLE_FIELD = new EntityField("tag.userVisible", "User Visible");

    private String orgId;
    private String eventId;
    private String tagTypeId;
    private String displayOrder;
    private boolean userVisible;

    private TagType tagType; // transient
    private String eventName; // transient


    public OrgTag() { }
    public OrgTag(String name, TagType tagType)
    {
        super(requireNonNull(name));
        setTagType(requireNonNull(tagType));
        setOrgId(tagType.getOrgId());
        setTagTypeId(tagType.getId());
    }

    public OrgTag(String name, TagType tagType, String eventId)
    {
        this(name, tagType);
        setEventId(requireNonNull(eventId));
    }

    public String get(EntityField field)
    {
        if (DISPLAY_ORDER_FIELD.equals(field)) return getDisplayOrder();
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (DISPLAY_ORDER_FIELD.equals(field)) setDisplayOrder(value);
        else super.set(field, value);
    }

    public int compareTo(OrgTag that)
    {
        if (displayOrder == null && that.getDisplayOrder() != null) { return 1; }
        if (displayOrder != null && that.getDisplayOrder() == null) { return -1; }

        int displayOrderCompare = displayOrder == null ? 0 : getDisplayOrder().compareTo(that.getDisplayOrder());
        return displayOrderCompare == 0 ? getName().compareToIgnoreCase(that.getName()) : displayOrderCompare;
    }
    
    @DynamoDBIgnore
    public boolean isTagType(String tagTypeName)
    {
        return (tagType != null && tagType.isTagType(tagTypeName)); // null-check to handle old data
    }

    @DynamoDBIgnore public boolean isEntityType(EntityType entityType)
    {
        return (tagType != null && tagType.isEntityType(entityType));
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

    @DynamoDBAttribute(attributeName = "TagTypeId")
    public String getTagTypeId()
    {
        return tagTypeId;
    }
    public void setTagTypeId(String tagTypeId)
    {
        this.tagTypeId = tagTypeId;
    }

    @DynamoDBAttribute(attributeName = "DisplayOrder")
    public String getDisplayOrder()
    {
        return displayOrder;
    }
    public void setDisplayOrder(String displayOrder)
    {
        this.displayOrder = displayOrder;
    }

    @DynamoDBAttribute(attributeName = "UserVisible")
    public boolean getUserVisible()
    {
        return userVisible;
    }
    public void setUserVisible(boolean userVisible)
    {
        this.userVisible = userVisible;
    }

    @DynamoDBIgnore
    public TagType getTagType()
    {
        return tagType;
    }
    public void setTagType(TagType tagType)
    {
        this.tagType = tagType;
    }

    @DynamoDBIgnore
    public String getEventName()
    {
        return eventName;
    }
    public void setEventName(String eventName) { this.eventName = eventName; }
}



