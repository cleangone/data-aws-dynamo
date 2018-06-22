package xyz.cleangone.data.aws.dynamo.entity.organization;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseMixinEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseNamedEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;

import static java.util.Objects.requireNonNull;

@DynamoDBTable(tableName="TagType")
public class TagType extends BaseNamedEntity implements Comparable<TagType>
{
    public static final String CATEGORY_TAG_TYPE = "Category";
    public static final String PERSON_TAG_TAG_TYPE = "Person Tag";

    public static final EntityField TAG_NAME_FIELD = new EntityField(NAME_FIELD, "Tag Type Name");
    public static final EntityField ENTITY_TYPE_FIELD = new EntityField("tagType.entityType", "Entity Type");
    public static final EntityField DISPLAY_ORDER_FIELD = new EntityField("tagType.displayOrder", "Display Order");

    private String orgId;
    private EntityType entityType;
    private String displayOrder;

    public TagType() { }
    public TagType(String name, EntityType entityType, String orgId)
    {
        super(requireNonNull(name));
        setEntityType(requireNonNull(entityType));
        setOrgId(requireNonNull(orgId));
    }

    public String get(EntityField field)
    {
        if (DISPLAY_ORDER_FIELD.equals(field)) return getDisplayOrder();
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (DISPLAY_ORDER_FIELD.equals(field)) setDisplayOrder(value);
        else super.set(field, value);
    }

    @DynamoDBIgnore public boolean isTagType(String name)
    {
        return name.equals(getName());
    }
    @DynamoDBIgnore public boolean isDefaultTagType()
    {
        return (isTagType(CATEGORY_TAG_TYPE) || isTagType(PERSON_TAG_TAG_TYPE));
    }

    @DynamoDBIgnore public boolean isEntityType(EntityType entityType)
    {
        return this.entityType == entityType;
    }
    @DynamoDBIgnore public String getEntityTypeString()
    {
        return entityType.toString();
    }

    @DynamoDBIgnore
    public String getHeaderName()
    {
        return getName() + "(s)";
    }

    public int compareTo(TagType that)
    {
        if (displayOrder == null && that.getDisplayOrder() != null) { return 1; }
        if (displayOrder != null && that.getDisplayOrder() == null) { return -1; }

        int displayOrderCompare = displayOrder == null ? 0 : getDisplayOrder().compareTo(that.getDisplayOrder());
        return displayOrderCompare == 0 ? getName().compareToIgnoreCase(that.getName()) : displayOrderCompare;
    }

    @DynamoDBAttribute(attributeName = "OrgId")
    public String getOrgId()
    {
        return orgId;
    }
    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    @DynamoDBAttribute(attributeName = "DisplayOrder")
    public String getDisplayOrder()
    {
        return displayOrder;
    }
    public void setDisplayOrder(String displayOrder)
    {
        this.displayOrder = displayOrder;
    }

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    @DynamoDBAttribute(attributeName = "EntityType")
    public EntityType getEntityType()
    {
        return entityType;
    }
    public void setEntityType(EntityType entityType)
    {
        this.entityType = entityType;
    }
}



