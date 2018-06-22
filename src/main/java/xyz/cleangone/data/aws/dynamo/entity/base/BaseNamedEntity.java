package xyz.cleangone.data.aws.dynamo.entity.base;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "NamedDummy")
public class BaseNamedEntity extends BaseEntity
{
    public static final EntityField NAME_FIELD = new EntityField("baseNamedEntity.name", "Name");

    protected String name;

    public BaseNamedEntity()
    {
        super();
    }

    public BaseNamedEntity(String name)
    {
        this();
        setName(name);
    }

    public String get(EntityField field)
    {
        if (NAME_FIELD.equals(field)) { return getName(); }
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (NAME_FIELD.equals(field)) { setName(value); }
        else super.set(field, value);
    }

    @DynamoDBAttribute(attributeName="Name")
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BaseNamedEntity)) return false;
        if (!super.equals(o)) return false;

        BaseNamedEntity that = (BaseNamedEntity) o;

        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}



