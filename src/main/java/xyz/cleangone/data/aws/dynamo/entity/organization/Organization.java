package xyz.cleangone.data.aws.dynamo.entity.organization;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.image.ImageContainer;

@DynamoDBTable(tableName="Organization")
public class Organization extends BaseOrg implements ImageContainer
{
    public static final EntityField LEFT_WIDTH_FIELD = new EntityField("org.leftColWidth", "Left Col. Width");
    public static final EntityField CENTER_WIDTH_FIELD = new EntityField("org.centerColWidth", "Center Col. Width");
    public static final EntityField RIGHT_WIDTH_FIELD = new EntityField("org.rightColWidth", "Right Col. Width");
    public static final EntityField MAX_LEFT_WIDTH_FIELD = new EntityField("org.maxLeftColWidth", "Max Left Col. Width");
    public static final EntityField MAX_CENTER_WIDTH_FIELD = new EntityField("org.maxCenterColWidth", "Max Center Col. Width");
    public static final EntityField MAX_RIGHT_WIDTH_FIELD = new EntityField("org.maxRightColWidth", "Max Right Col. Width");
    public static final EntityField EVENT_CAPTION_FIELD = new EntityField("org.eventCaption", "Event Caption");
    public static final EntityField EVENT_CAPTION_PLURAL_FIELD = new EntityField("org.eventCaptionPlural", "Event Caption Plural");

    private int leftColWidth;
    private int centerColWidth;
    private int rightColWidth;
    private int maxLeftColWidth;
    private int maxCenterColWidth;
    private int maxRightColWidth;

    private String paymentProcessorId;
    private String eventCaption;   // display name for Events - for when they are used to manage some other type of thing
    private String eventCaptionPlural;

    public Organization()
    {
        super();
    }
    public Organization(String name)
    {
        super(name);
    }


    public String get(EntityField field)
    {
        if (EVENT_CAPTION_FIELD.equals(field)) return getEventCaption();
        else if (EVENT_CAPTION_PLURAL_FIELD.equals(field)) return getEventCaptionPlural();
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (EVENT_CAPTION_FIELD.equals(field)) setEventCaption(value);
        else if (EVENT_CAPTION_PLURAL_FIELD.equals(field)) setEventCaptionPlural(value);
        else super.set(field, value);
    }

    public int getInt(EntityField field)
    {
        if (LEFT_WIDTH_FIELD.equals(field)) return getLeftColWidth();
        else if (CENTER_WIDTH_FIELD.equals(field)) return getCenterColWidth();
        else if (RIGHT_WIDTH_FIELD.equals(field)) return getRightColWidth();
        else if (MAX_LEFT_WIDTH_FIELD.equals(field)) return getMaxLeftColWidth();
        else if (MAX_CENTER_WIDTH_FIELD.equals(field)) return getMaxCenterColWidth();
        else if (MAX_RIGHT_WIDTH_FIELD.equals(field)) return getMaxRightColWidth();
        else return super.getInt(field);
    }

    public void setInt(EntityField field, int value)
    {
        if (LEFT_WIDTH_FIELD.equals(field)) setLeftColWidth(value);
        else if (CENTER_WIDTH_FIELD.equals(field)) setCenterColWidth(value);
        else if (RIGHT_WIDTH_FIELD.equals(field)) setRightColWidth(value);
        else if (MAX_LEFT_WIDTH_FIELD.equals(field)) setMaxLeftColWidth(value);
        else if (MAX_CENTER_WIDTH_FIELD.equals(field)) setMaxCenterColWidth(value);
        else if (MAX_RIGHT_WIDTH_FIELD.equals(field)) setMaxRightColWidth(value);
        else super.setInt(field, value);
    }

    @DynamoDBIgnore
    public boolean colWidthsSet()
    {
        return (leftColWidth != 0 || centerColWidth != 0 || rightColWidth != 0);
    }

    @DynamoDBAttribute(attributeName = "LeftColWidth")
    public int getLeftColWidth()
    {
        return leftColWidth;
    }
    public void setLeftColWidth(int leftColWidth)
    {
        this.leftColWidth = leftColWidth;
    }

    @DynamoDBAttribute(attributeName = "CenterColWidth")
    public int getCenterColWidth()
    {
        return centerColWidth;
    }
    public void setCenterColWidth(int centerColWidth)
    {
        this.centerColWidth = centerColWidth;
    }

    @DynamoDBAttribute(attributeName = "RightColWidth")
    public int getRightColWidth()
    {
        return rightColWidth;
    }
    public void setRightColWidth(int rightColWidth)
    {
        this.rightColWidth = rightColWidth;
    }

    @DynamoDBAttribute(attributeName = "MaxLeftColWidth")
    public int getMaxLeftColWidth()
    {
        return maxLeftColWidth;
    }
    public void setMaxLeftColWidth(int maxLeftColWidth)
    {
        this.maxLeftColWidth = maxLeftColWidth;
    }

    @DynamoDBAttribute(attributeName = "MaxCenterColWidth")
    public int getMaxCenterColWidth()
    {
        return maxCenterColWidth;
    }
    public void setMaxCenterColWidth(int maxCenterColWidth)
    {
        this.maxCenterColWidth = maxCenterColWidth;
    }

    @DynamoDBAttribute(attributeName = "MaxRightColWidth")
    public int getMaxRightColWidth()
    {
        return maxRightColWidth;
    }
    public void setMaxRightColWidth(int maxRightColWidth)
    {
        this.maxRightColWidth = maxRightColWidth;
    }

    @DynamoDBAttribute(attributeName = "PaymentProcessorId")
    public String getPaymentProcessorId()
    {
        return paymentProcessorId;
    }
    public void setPaymentProcessorId(String paymentProcessorId)
    {
        this.paymentProcessorId = paymentProcessorId;
    }

    @DynamoDBAttribute(attributeName = "EventCaption")
    public String getEventCaption()
    {
        return eventCaption;
    }
    public void setEventCaption(String eventCaption)
    {
        this.eventCaption = eventCaption;
    }

    @DynamoDBAttribute(attributeName = "EventCaptionPlural")
    public String getEventCaptionPlural()
    {
        return eventCaptionPlural;
    }
    public void setEventCaptionPlural(String eventCaptionPlural)
    {
        this.eventCaptionPlural = eventCaptionPlural;
    }

    public String getS3LinkPrefix(Organization org, String filePath)
    {
        return "org/" + org.getTag();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Organization)) return false;
        if (!super.equals(o)) return false;

        Organization that = (Organization) o;

        if (getLeftColWidth() != that.getLeftColWidth()) return false;
        if (getCenterColWidth() != that.getCenterColWidth()) return false;
        return getRightColWidth() == that.getRightColWidth();
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + getLeftColWidth();
        result = 31 * result + getCenterColWidth();
        result = 31 * result + getRightColWidth();
        return result;
    }
}


