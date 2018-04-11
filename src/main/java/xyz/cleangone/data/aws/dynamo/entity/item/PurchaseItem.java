package xyz.cleangone.data.aws.dynamo.entity.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;

import java.util.Date;

import static java.util.Objects.requireNonNull;

@DynamoDBTable(tableName="PurchaseItem")
public class PurchaseItem extends BaseItem
{

    // todo - bidding goes in CatalogItem
    public enum SaleType { Purchase, Bid }

    public static final EntityField IS_BID_FIELD = new EntityField("item.isBid", "Is Bid");
    public static final EntityField QUANTITY_FIELD = new EntityField("item.quantity", "Quantity");
    public static final EntityField AVAIL_START_FIELD = new EntityField("item.availabilityStart", "Start Date");
    public static final EntityField AVAIL_END_FIELD = new EntityField("item.availabilityEnd", "End Date");

    private String eventId;
    private SaleType saleType = SaleType.Purchase;
    private Integer quantity;
    private Date availabilityStart;
    private Date availabilityEnd;

    public PurchaseItem() {}
    public PurchaseItem(String name, String eventId)
    {
        super(requireNonNull(name));
        setEventId(requireNonNull(eventId));
    }

    public boolean getBoolean(EntityField field)
    {
        if (IS_BID_FIELD.equals(field)) return getSaleType() == SaleType.Bid;
        else return super.getBoolean(field);
    }

    public void setBoolean(EntityField field, boolean value)
    {
        if (IS_BID_FIELD.equals(field)) setSaleType(value ? SaleType.Bid : SaleType.Purchase);
        else super.setBoolean(field, value);
    }

    public Integer getInteger(EntityField field)
    {
        if (QUANTITY_FIELD.equals(field)) return getQuantity();
        else return super.getInteger(field);
    }

    public void setInteger(EntityField field, Integer value)
    {
        if (QUANTITY_FIELD.equals(field)) setQuantity(value);
        else super.setInteger(field, value);
    }

    public Date getDate(EntityField field)
    {
        if (AVAIL_START_FIELD.equals(field)) return getAvailabilityStart();
        else if (AVAIL_END_FIELD.equals(field)) return getAvailabilityEnd();
        else return super.getDate(field);
    }

    public void setDate(EntityField field, Date value)
    {
        if (AVAIL_START_FIELD.equals(field)) setAvailabilityStart(value);
        else if (AVAIL_END_FIELD.equals(field)) setAvailabilityEnd(value);
        else super.setDate(field, value);
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

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    public SaleType getSaleType()
    {
        return saleType;
    }
    public void setSaleType(SaleType saleType)
    {
        this.saleType = saleType;
    }

    @DynamoDBAttribute(attributeName = "Quantity")
    public Integer getQuantity()
    {
        return quantity;
    }
    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    @DynamoDBAttribute(attributeName = "AvailabilityStart")
    public Date getAvailabilityStart()
    {
        return availabilityStart;
    }
    public void setAvailabilityStart(Date availabilityStart)
    {
        this.availabilityStart = availabilityStart;
    }

    @DynamoDBAttribute(attributeName = "AvailabilityEnd")
    public Date getAvailabilityEnd()
    {
        return availabilityEnd;
    }
    public void setAvailabilityEnd(Date availabilityEnd)
    {
        this.availabilityEnd = availabilityEnd;
    }
}



