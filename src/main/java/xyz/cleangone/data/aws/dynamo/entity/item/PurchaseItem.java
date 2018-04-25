package xyz.cleangone.data.aws.dynamo.entity.item;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;

import java.util.Date;

import static java.util.Objects.requireNonNull;

@DynamoDBTable(tableName="PurchaseItem")
public class PurchaseItem extends BaseItem
{
    public static final EntityField QUANTITY_FIELD = new EntityField("item.quantity", "Quantity");

    protected String eventId;
    protected Integer quantity;

    public PurchaseItem() {}
    public PurchaseItem(String name, String eventId)
    {
        super(requireNonNull(name));
        setEventId(requireNonNull(eventId));
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

    @DynamoDBAttribute(attributeName = "EventId")
    public String getEventId()
    {
        return eventId;
    }
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
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

}



