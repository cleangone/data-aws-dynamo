package xyz.cleangone.data.aws.dynamo.entity.base;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

// experimenting with the concept of having optional fields
// if a child class never uses a field, it will not be stored
@DynamoDBTable(tableName = "MixinDummy")
public class BaseMixinEntity extends BaseNamedEntity
{
    public static final EntityField ENABLED_FIELD = new EntityField("mixin.enabled", "Enabled");
    public static final EntityField NOTES_FIELD = new EntityField("mixin.notes", "Notes");

    protected boolean enabled;
    protected String notes;

    public BaseMixinEntity()
    {
        super();
    }
    public BaseMixinEntity(String name)
    {
        super(name);
    }

    @DynamoDBIgnore public String getEnabledString()
    {
        return (enabled ? "Enabled" : "Disabled");
    }

    public String get(EntityField field)
    {
        if (NOTES_FIELD.equals(field)) { return getNotes(); }
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (NOTES_FIELD.equals(field)) { setNotes(value); }
        else super.set(field, value);
    }

    public boolean getBoolean(EntityField field)
    {
        if (ENABLED_FIELD.equals(field)) return getEnabled();
        else return super.getBoolean(field);
    }

    public void setBoolean(EntityField field, boolean value)
    {
        if (ENABLED_FIELD.equals(field)) setEnabled(value);
        else super.setBoolean(field, value);
    }

    @DynamoDBAttribute(attributeName="Enabled")
    public boolean getEnabled()
    {
        return enabled;
    }
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @DynamoDBAttribute(attributeName="Notes")
    public String getNotes()
    {
        return notes;
    }
    public void setNotes(String notes)
    {
        this.notes = notes;
    }
}



