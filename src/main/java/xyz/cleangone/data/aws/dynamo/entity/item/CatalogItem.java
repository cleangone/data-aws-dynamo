package xyz.cleangone.data.aws.dynamo.entity.item;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.image.ImageContainer;
import xyz.cleangone.data.aws.dynamo.entity.bid.ItemBid;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@DynamoDBTable(tableName="CatalogItem")
public class CatalogItem extends PurchaseItem implements ImageContainer
{
    public static final EntityField CATEGORIES_FIELD      = new EntityField("item.categories", "Categories");
    public static final EntityField SALE_TYPE_FIELD       = new EntityField("item.saleType", "Sale Type");
    public static final EntityField SALE_STATUS_FIELD     = new EntityField("item.saleStatus", "Status");
    public static final EntityField COMBINED_STATUS_FIELD = new EntityField("item.combinedStatus", "Status");
    public static final EntityField AVAIL_START_FIELD     = new EntityField("item.availabilityStart", "Start Date");
    public static final EntityField AVAIL_END_FIELD       = new EntityField("item.availabilityEnd", "End Date");
    public static final EntityField DROP_WINDOW_FIELD     = new EntityField("item.dropWindow", "Drop Window (Min)");
    public static final EntityField RELATIVE_WIDTH_FIELD  = new EntityField("item.relativeWidth", "Relative Width");
    public static long ONE_MINUTE = 1000*60;
    private static String CATEGORY_TAG_TYPE_ID_PLACEHOLDER = "category";

    private String orgId;
    protected SaleType saleType = SaleType.Purchase;
    protected SaleStatus saleStatus = SaleStatus.Preview;
    protected Date availabilityStart;
    protected Date availabilityEnd;

    protected Date auctionEnd;  // todo - should add this - an item can still be available after the auction ends

    private BigDecimal startPrice;
    private Integer dropWindow; // minutes, can be null
    private Integer relativeWidth; // 2 = 2x normal, etc, null is default
    private List<S3Link> images;
    private List<String> categoryIds;
    private List<String> tagIds; // tags other than category - may change to Map<String, List<String>> tagTypeIdToTagIds;
    private String highBidId;
    private String highBidderId;  // denormalized for speed
    private Map<String, String> tagTypeIdToTagsCsv = new HashMap<>(); // transient

    public CatalogItem() {}
    public CatalogItem(String name, String orgId, String eventId)
    {
        super(requireNonNull(name), eventId);
        setOrgId(requireNonNull(orgId));
    }

    @DynamoDBIgnore
    public void bid(ItemBid bid)
    {
        // todo - should check that a bid not possibly past end date
        setPrice(bid.getCurrAmount());
        setHighBidId(bid.getId());
        setHighBidderId(bid.getUserId());  // replicated for speed

        Date now = new Date();
        if (availabilityEnd != null &&
            now.getTime() > availabilityEnd.getTime() - ONE_MINUTE &&
            now.getTime() < availabilityEnd.getTime())
        {
            setAvailabilityEnd(new Date(now.getTime() + ONE_MINUTE));
        }
    }

    @DynamoDBIgnore
    public boolean isVisible()
    {
        return (enabled &&
            !isPreview() &&
            (availabilityStart == null || availabilityStart.before(new Date())));
    }

    @DynamoDBIgnore public boolean hasBids()     { return highBidId != null; }
    @DynamoDBIgnore public boolean isPurchase()  { return saleType == SaleType.Purchase; }
    @DynamoDBIgnore public boolean isAuction()   { return saleType == SaleType.Auction; }
    @DynamoDBIgnore public boolean isDrop()      { return saleType == SaleType.Drop; }
    @DynamoDBIgnore public boolean isPreview()   { return saleStatus == SaleStatus.Preview; }
    @DynamoDBIgnore public boolean isAvailable() { return saleStatus == SaleStatus.Available; }
    @DynamoDBIgnore public boolean isSold()      { return saleStatus == SaleStatus.Sold; }
    @DynamoDBIgnore public boolean isUnsold()    { return saleStatus == SaleStatus.Unsold; }

    @DynamoDBIgnore public String getSaleTypeString() { return saleType.toString(); }
    @DynamoDBIgnore public String getSaleStatusString() { return saleStatus.toString(); }
    @DynamoDBIgnore public String getCombinedStatus()
    {
        return (getEnabledString() + " / " + getSaleTypeString() + " / " + getSaleStatusString());
    }

    @DynamoDBIgnore public boolean isInDropWindow()
    {
        Date dropWindowEnd = getDropWindowEnd() ;
        return dropWindowEnd != null && dropWindowEnd.after(new Date());
    }

    @DynamoDBIgnore public Date getDropWindowEnd()
    {
        return  (isDrop() && availabilityStart != null && dropWindow != null) ? new Date(availabilityStart.getTime() + dropWindow * ONE_MINUTE) : null;
    }

    public Integer getInteger(EntityField field)
    {
        if (DROP_WINDOW_FIELD.equals(field)) return getDropWindow();
        if (RELATIVE_WIDTH_FIELD.equals(field)) return getRelativeWidth();
        else return super.getInteger(field);
    }

    public void setInteger(EntityField field, Integer value)
    {
        if (DROP_WINDOW_FIELD.equals(field)) setDropWindow(value);
        if (RELATIVE_WIDTH_FIELD.equals(field)) setRelativeWidth(value);
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

    @DynamoDBAttribute(attributeName = "OrgId")
    public String getOrgId()
    {
        return orgId;
    }
    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    @DynamoDBAttribute(attributeName = "SaleType")
    public SaleType getSaleType()
    {
        return saleType;
    }
    public void setSaleType(SaleType saleType)
    {
        this.saleType = saleType;
    }

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    @DynamoDBAttribute(attributeName = "SaleStatus")
    public SaleStatus getSaleStatus()
    {
        return saleStatus;
    }
    public void setSaleStatus(SaleStatus saleStatus)
    {
        this.saleStatus = saleStatus;
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
        return (availabilityEnd == null && isDrop()) ? getDropWindowEnd() : availabilityEnd;
    }
    public void setAvailabilityEnd(Date availabilityEnd)
    {
        this.availabilityEnd = availabilityEnd;
    }

    @DynamoDBAttribute(attributeName = "DropWindow")
    public Integer getDropWindow()
    {
        return dropWindow;
    }
    public void setDropWindow(Integer dropWindow)
    {
        this.dropWindow = dropWindow;
    }

    @DynamoDBAttribute(attributeName = "RelativeWidth")
    public Integer getRelativeWidth()
    {
        return relativeWidth;
    }
    public void setRelativeWidth(Integer relativeWidth)
    {
        this.relativeWidth = relativeWidth;
    }

    @DynamoDBAttribute(attributeName="Images")
    public List<S3Link> getImages()
    {
        return images;
    }
    public void setImages(List<S3Link> images)
    {
        this.images = images;
    }
    public void addImage(S3Link image)
    {
        if (images == null) { images = new ArrayList<>(); }
        images.add(image);
    }
    public void deleteImage(S3Link image)
    {
        images.remove(image);
    }

    @DynamoDBAttribute(attributeName="CategoryIds")
    public List<String> getCategoryIds() { if (categoryIds == null) { categoryIds = new ArrayList<>(); } return categoryIds; }
    public void setCategoryIds(List<String> categoryIds)
    {
        this.categoryIds = categoryIds;
    }
    public void addCategoryId(String categoryId)
    {
        if (!getCategoryIds().contains(categoryId)) { categoryIds.add(categoryId); }
    }
    public void removeCategoryId(String categoryId)
    {
        categoryIds.remove(categoryId);
    }

    @DynamoDBAttribute(attributeName="TagIds")
    public List<String> getTagIds() { if (tagIds == null) { tagIds = new ArrayList<>(); } return tagIds; }
    public void setTagIds(List<String> tagIds)
    {
        this.tagIds = tagIds;
    }
    public void addTagId(String tagId)
    {
        if (!getTagIds().contains(tagId)) { tagIds.add(tagId); }
    }
    public void removeTagId(String tagId)
    {
        getTagIds().remove(tagId);
    }

    @DynamoDBAttribute(attributeName="HighBidId")
    public String getHighBidId()
    {
        return highBidId;
    }
    public void setHighBidId(String highBidId)
    {
        this.highBidId = highBidId;
    }

    @DynamoDBAttribute(attributeName="HighBidderId")
    public String getHighBidderId()
    {
        return highBidderId;
    }
    public void setHighBidderId(String highBidderId)
    {
        this.highBidderId = highBidderId;
    }

//    @DynamoDBIgnore public void setCategoryTagTypeId(String categoryTagTypeId) { this.categoryTagTypeId = categoryTagTypeId; }

//    @DynamoDBIgnore
//    public List<String> getCategoryIds()
//    {
//        return getTagIds().stream()
//            .filter(t -> t.getTagTypeId().equals(tagTypeId)) // filter for specified tageTypeId
//            .map(OrgTag::getName)
//            .collect(Collectors.toList());
//
//
//
//        if (categoryIds == null) { categoryIds = new ArrayList<>(); }
//        return categoryIds;
//    }


    @DynamoDBIgnore
    public String getTagsCsv(String tagTypeId) { return tagTypeIdToTagsCsv.get(tagTypeId); }
    public void setTagsCsv(String tagTypeId, Map<String, OrgTag> tagIdToTag)
    {
        tagTypeIdToTagsCsv.put(tagTypeId, getCsv(tagTypeId, getTagIds(), tagIdToTag));
    }

    @DynamoDBIgnore
    public String getCategoriesCsv() { return tagTypeIdToTagsCsv.get(CATEGORY_TAG_TYPE_ID_PLACEHOLDER); }
    public void setCategorieCsv(Map<String, OrgTag> tagIdToTag)
    {
        tagTypeIdToTagsCsv.put(CATEGORY_TAG_TYPE_ID_PLACEHOLDER, getCsv(null, getCategoryIds(), tagIdToTag));
    }

    private String getCsv(String tagTypeId, List<String> tagIds, Map<String, OrgTag> tagIdToTag)
    {
        if (tagIds.isEmpty()) { return ""; }

        List<String> tagNames = tagIds.stream()
            .filter(id -> tagIdToTag.containsKey(id)) // map may be of a subset of tags
            .map(id -> tagIdToTag.get(id))
            .filter(t -> (tagTypeId == null || t.getTagTypeId().equals(tagTypeId))) // filter for tagTypeId if specified
            .map(OrgTag::getName)
            .collect(Collectors.toList());

        Collections.sort(tagNames);
        return tagNames.stream().collect(Collectors.joining(", "));
    }
}



