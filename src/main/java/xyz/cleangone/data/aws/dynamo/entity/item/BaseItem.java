package xyz.cleangone.data.aws.dynamo.entity.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseNamedEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;

import java.math.BigDecimal;

@DynamoDBTable(tableName = "ItemDummy")
public class BaseItem extends BaseNamedEntity
{
    public static final EntityField PRICE_FIELD = new EntityField("item.price", "Price");

    private BigDecimal price;

    public BaseItem() {}
    public BaseItem(String name)
    {
        super(name);
    }

    @DynamoDBIgnore
    public String getDisplayPrice()
    {
        return price == null ? "" : "$" + price;
    }

    public String get(EntityField field)
    {
        return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        super.set(field, value);
    }

    public BigDecimal getBigDecimal(EntityField field)
    {
        if (PRICE_FIELD.equals(field)) return getPrice();
        else return super.getBigDecimal(field);
    }

    public void setBigDecimal(EntityField field, BigDecimal value)
    {
        if (PRICE_FIELD.equals(field)) setPrice(value);
        else super.setBigDecimal(field, value);
    }

    @DynamoDBAttribute(attributeName = "Price")
    public BigDecimal getPrice()
    {
        return price;
    }
    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }
}



