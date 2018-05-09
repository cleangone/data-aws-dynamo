package xyz.cleangone.data.aws.dynamo.entity.person;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseMixinEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;

@DynamoDBTable(tableName="Address")
public class Address extends BaseMixinEntity
{
    public static final EntityField ADDRESS_FIELD = new EntityField("Address.streetAddress", "Address");
    public static final EntityField CITY_FIELD = new EntityField("Address.city", "City");
    public static final EntityField STATE_FIELD = new EntityField("Address.state", "State");
    public static final EntityField ZIP_FIELD = new EntityField("Address.zip", "Zip");

    private String streetAddress;
    private String city;
    private String state;
    private String zip;

    public String get(EntityField field)
    {
        if (ADDRESS_FIELD.equals(field)) return getStreetAddress();
        else if (CITY_FIELD.equals(field)) return getCity();
        else if (STATE_FIELD.equals(field)) return getState();
        else if (ZIP_FIELD.equals(field)) return getZip();
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (ADDRESS_FIELD.equals(field)) setStreetAddress(value);
        else if (CITY_FIELD.equals(field)) setCity(value);
        else if (STATE_FIELD.equals(field)) setState(value);
        else if (ZIP_FIELD.equals(field)) setZip(value);
        else super.set(field, value);
    }

    @DynamoDBAttribute(attributeName="StreetAddress")
    public String getStreetAddress()
    {
        return streetAddress;
    }
    public void setStreetAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
    }

    @DynamoDBAttribute(attributeName="City")
    public String getCity()
    {
        return city;
    }
    public void setCity(String city)
    {
        this.city = city;
    }

    @DynamoDBAttribute(attributeName="State")
    public String getState()
    {
        return state;
    }
    public void setState(String state)
    {
        this.state = state;
    }

    @DynamoDBAttribute(attributeName="Zip")
    public String getZip()
    {
        return zip;
    }
    public void setZip(String zip)
    {
        this.zip = zip;
    }
}


