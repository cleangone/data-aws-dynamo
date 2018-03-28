package xyz.cleangone.data.aws.dynamo.entity.base;


public class EntityField
{
    private String name;
    private String displayName;

    public EntityField(String name)
    {
        this.name = name;
    }

    public EntityField(String name, String displayName)
    {
        this.name = name;
        this.displayName = displayName;
    }

    public EntityField(EntityField entityField, String displayName)
    {
        this.name = entityField.getName();
        this.displayName = displayName;
    }

    public EntityField noDisplayName()
    {
        return (displayName == null ? this : new EntityField(name));
    }

    public String getName()
    {
        return name;
    }
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String toString()
    {
        return "EntityField{" +
            "name='" + name + '\'' +
            ", displayName='" + displayName + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityField that = (EntityField) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
