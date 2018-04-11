package xyz.cleangone.data.aws.dynamo.entity.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.base.ImageContainer;
import xyz.cleangone.data.aws.dynamo.entity.bid.ItemBid;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@DynamoDBTable(tableName="CatalogItem")
public class CatalogItem extends PurchaseItem implements ImageContainer
{
    public static final EntityField CATEGORIES_FIELD = new EntityField("item.categories", "Categories");

    private String orgId;
    private List<S3Link> images;
    private List<String> categoryIds;
    private String highBidId;

    private String categoriesCsv; // transient

    public CatalogItem() {}
    public CatalogItem(String name, String orgId, String eventId)
    {
        super(requireNonNull(name), eventId);
        setOrgId(requireNonNull(orgId));
    }

    @DynamoDBIgnore
    public boolean isAvailable()
    {
        return getQuantity() == null || getQuantity() > 0;
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
    public List<String> getCategoryIds()
    {
        if (categoryIds == null) { categoryIds = new ArrayList<>(); }
        return categoryIds;
    }
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

    @DynamoDBAttribute(attributeName="HighBidId")
    public String getHighBidId()
    {
        return highBidId;
    }
    public void setHighBidId(String highBidId)
    {
        this.highBidId = highBidId;
    }

    @DynamoDBIgnore
    public String getCategoriesCsv() { return categoriesCsv; }
    public void setCategoriesCsv(Map<String, OrgTag> tagsById)
    {
        categoriesCsv = getCsv(tagsById);
    }

    private String getCsv(Map<String, OrgTag> tagsById)
    {
        if (getCategoryIds().isEmpty()) { return ""; }

        List<String> categoryNames = categoryIds.stream()
            .filter(id -> tagsById.containsKey(id)) // map may be of a subset of tags
            .map(id -> tagsById.get(id))
            .map(OrgTag::getName)
            .collect(Collectors.toList());

        Collections.sort(categoryNames);
        return categoryNames.stream().collect(Collectors.joining(", "));
    }
}



