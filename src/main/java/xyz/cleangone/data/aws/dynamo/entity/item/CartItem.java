package xyz.cleangone.data.aws.dynamo.entity.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import xyz.cleangone.data.aws.dynamo.entity.action.Action;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.organization.EventParticipant;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgEvent;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

// not persisted (yet), but extends entity for grid generation
public class CartItem extends BaseItem
{
    public static final EntityField CART_ITEM_NAME_FIELD = new EntityField(NAME_FIELD, "Item");
    public static final EntityField PRICE_FIELD = new EntityField("cartItem.price", "");

    private OrgEvent event;
    private OrgTag category;
    private EventParticipant targetParticipant;
    private Action pledge;
    private String pledgeFulfillmentActionDesc;
    private CatalogItem catalogItem;

    public CartItem(String name, BigDecimal price, OrgEvent event)
    {
        setName(requireNonNull(name));
        setPrice(requireNonNull(price));
        this.event = requireNonNull(event);

        id = UUID.randomUUID().toString();  // uniqueness in cart
    }

    public CartItem(CatalogItem catalogItem, OrgEvent event, OrgTag category)
    {
        this(catalogItem.getName(), catalogItem.getPrice(), event);
        this.catalogItem = catalogItem;
        this.category = category;
    }

    public CartItem(String name, BigDecimal price, OrgEvent event, EventParticipant targetParticipant)
    {
        this(name, price, event);
        this.targetParticipant = targetParticipant;
    }

    public CartItem(String name, BigDecimal price, OrgEvent event, EventParticipant targetParticipant,
        Action pledge, String pledgeFullfillmentActionDesc)
    {
        this(name, price, event, targetParticipant);
        this.pledge = requireNonNull(pledge);
        this.pledgeFulfillmentActionDesc = pledgeFullfillmentActionDesc;
    }

    @DynamoDBIgnore
    public boolean isDonationOrPledgeFulfillment() { return catalogItem == null; }

    @DynamoDBIgnore
    public boolean isDonation() { return (catalogItem == null && pledge == null); }

    @DynamoDBIgnore
    public boolean isPledgeFulfillment()
    {
        return (catalogItem == null && pledge != null);
    }

    public OrgEvent getEvent()
    {
        return event;
    }
    public OrgTag getCategory()
    {
        return category;
    }
    public EventParticipant getTargetParticipant()
    {
        return targetParticipant;
    }
    public String getPledgeFulfillmentActionDesc()
    {
        return pledgeFulfillmentActionDesc;
    }
    public CatalogItem getCatalogItem()
    {
        return catalogItem;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        if (!super.equals(o)) return false;

        CartItem cartItem = (CartItem) o;
        return id.equals(cartItem.getId());
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}


