package xyz.cleangone.data.aws.dynamo.entity.organization;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;

import java.util.Objects;


// todo - control ordering and whether sections are displayed in the admin nav col

@DynamoDBDocument
public class EventAdminSection
{
    public static final String GENERAL = "General";
    public static final String PARTICIPANTS = "Participants";
    public static final String TAGS = "Tags";
    public static final String CATEGORIES = "Categories";
    public static final String ITEMS = "Items";
    public static final String DATES = "Dates";
    public static final String ROLES = "User Roles";
    public static final String USERS = "Users";
    public static final String DONATIONS = "Donations";
    public static final String PURCHASES = "Purchases";

    private String eventAdminSection;
    private String sortOrder;

    @DynamoDBIgnore public int getNumericSortOrder()
    {
        return (sortOrder == null ? -1 : new Integer(sortOrder));
    }
    @DynamoDBIgnore public void setNumericSortOrder(int sortOrder)
    {
        setSortOrder(sortOrder + "");
    }

    @DynamoDBAttribute(attributeName = "EventAdminSection")
    public String getEventAdminSection()
    {
        return eventAdminSection;
    }
    public void setEventAdminSection(String eventAdminSection)
    {
        this.eventAdminSection = eventAdminSection;
    }

    @DynamoDBAttribute(attributeName = "SortOrder")
    public String getSortOrder()
    {
        return sortOrder;
    }
    public void setSortOrder(String sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof EventAdminSection)) return false;

        EventAdminSection that = (EventAdminSection) o;

        return getEventAdminSection() != null ? getEventAdminSection().equals(that.getEventAdminSection()) : that.getEventAdminSection() == null;
    }

    @Override
    public int hashCode()
    {
        return getEventAdminSection() != null ? getEventAdminSection().hashCode() : 0;
    }
}
