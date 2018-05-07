package xyz.cleangone.data.aws.dynamo.entity.person;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseMixinEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;

@DynamoDBTable(tableName="BasePersonDummy")
public class BasePerson extends BaseMixinEntity
{
    public static final EntityField FIRST_NAME_FIELD = new EntityField("Person.firstName", "First Name");
    public static final EntityField LAST_NAME_FIELD = new EntityField("Person.lastName", "Last Name");

    private String firstName;
    private String lastName;

    public BasePerson() { }
    public BasePerson(String firstName, String lastName)
    {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @DynamoDBIgnore public String getFirstLast() { return firstName + " " +  lastName; }
    @DynamoDBIgnore public String getLastCommaFirst()
    {
        return lastName + ", " + firstName;
    }

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

    @DynamoDBAttribute(attributeName="FirstName")
    public String getFirstName() { return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}

    @DynamoDBAttribute(attributeName="LastName")
    public String getLastName() { return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
}



