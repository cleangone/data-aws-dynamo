package xyz.cleangone.data.manager.event;

import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import xyz.cleangone.data.aws.dynamo.dao.*;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.image.ImageType;
import xyz.cleangone.data.aws.dynamo.entity.item.CatalogItem;
import xyz.cleangone.data.aws.dynamo.entity.notification.NotificationType;
import xyz.cleangone.data.aws.dynamo.entity.notification.QueuedNotification;
import xyz.cleangone.data.aws.dynamo.entity.organization.*;
import xyz.cleangone.data.cache.OrgEntityCache;
import xyz.cleangone.data.manager.ImageContainerManager;
import xyz.cleangone.data.manager.ImageManager;
import xyz.cleangone.data.manager.TagManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ItemManager implements ImageContainerManager
{
    private static final OrgEntityCache<CatalogItem> ITEM_CACHE = new OrgEntityCache<>(EntityType.ITEM);

    private final CatalogItemDao itemDao = new CatalogItemDao();
    private final QueuedNotificationDao notificationDao = new QueuedNotificationDao();

    private Organization org;
    private OrgEvent event;
    private CatalogItem item;

    public ItemManager() { }
    public ItemManager(ItemManager that, CatalogItem item)
    {
        this(that.org, that.event, item);
    }
    public ItemManager(Organization org)
    {
        this.org = org;
    }
    public ItemManager(Organization org, OrgEvent event, CatalogItem item)
    {
        this.org = org;
        this.event = event;
        this.item = item;
    }

    public CatalogItem createItem(String name, String tagId)
    {
        CatalogItem item = new CatalogItem(name, org.getId(), event.getId());
        if (tagId != null) { item.addTagId(tagId); }

        itemDao.save(item);
        return item;
    }

    private List<CatalogItem> getOrgItems()
    {
        Date start = new Date();
        List<CatalogItem> items = ITEM_CACHE.get(org);
        if (items != null) { return items; }

        items = itemDao.getByOrg(org.getId());
        if (items == null) { items = new ArrayList<>(); }
        ITEM_CACHE.put(org, items, start);

        return items;
    }

    public CatalogItem getItemById(String itemId)
    {
        return itemDao.getById(itemId);
    }
    public CatalogItem getItem()
    {
        return item;
    }
    public void setItem(CatalogItem item)
    {
        this.item = item;
    }

    public List<CatalogItem> getItems()
    {
        return getOrgItems().stream()
            .filter(item -> item.getEventId().equals(event.getId()))
            .collect(Collectors.toList());
    }

    public List<CatalogItem> getItemsWithCategory(String tagId)
    {
        return getItems(item -> item.getCategoryIds().contains(tagId));
    }
    public List<CatalogItem> getItemsWithTag(String tagId)
    {
        return getItems(item -> item.getTagIds().contains(tagId));
    }
    private List<CatalogItem> getItems(Predicate<? super CatalogItem> predicate)
    {
        return getItems().stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }

    public List<CatalogItem> getItems(List<String> itemIds)
    {
        return getOrgItems().stream()
            .filter(item -> itemIds.contains(item.getId()))
            .collect(Collectors.toList());
    }

    public void addCategoryId(String categoryId, List<CatalogItem> items)
    {
        for (CatalogItem item : items)
        {
            item.addCategoryId(categoryId);
            itemDao.save(item);
        }
    }

    public void removeCategoryId(String categoryId, List<CatalogItem> items)
    {
        for (CatalogItem item : items)
        {
            item.removeCategoryId(categoryId);
            itemDao.save(item);
        }
    }

    public void addTagId(String tagId, List<CatalogItem> items)
    {
        for (CatalogItem item : items)
        {
            item.addTagId(tagId);
            itemDao.save(item);
        }
    }

    public void removeTagId(String tagId, List<CatalogItem> items)
    {
        for (CatalogItem item : items)
        {
            item.removeTagId(tagId);
            itemDao.save(item);
        }
    }

    public void save()
    {
        save(item);
    }
    public void save(CatalogItem item)
    {
        itemDao.save(item);

        if (item.getEnabled() &&
            item.isAvailable() &&
            (item.isAuction() || item.isDrop()) &&
            item.getAvailabilityEnd() != null &&
            item.getAvailabilityEnd().after(new Date()))
        {
            notificationDao.save(new QueuedNotification(item, NotificationType.ItemAuctionClose));
        }
    }

    public void delete(CatalogItem item)
    {
        itemDao.delete(item);
    }

    public List<S3Link> getImages()
    {
        return item.getImages();
    }
    public void addImage(S3Link image)
    {
        item.addImage(image);
    }
    public void deleteImage(S3Link image)
    {
        item.deleteImage(image);
    }
    public List<String> getImageUrls()
    {
        return getImageManager().getUrls();
    }
    public ImageManager getImageManager()
    {
        return new ImageManager(this);
    }
    public S3Link createS3Link(String filePath)
    {
        String separator = filePath.startsWith("/") ? "" : "/";

        String fullFilePath = "org/" + org.getTag() + "/events/" + event.getTag() + "/items/" + item.getId() + separator + filePath;
        return itemDao.createS3Link(fullFilePath);
    }

    // todo - not used yet
    public String getImageUrl(ImageType imageType)
    {
        return null;
    }
    public void setImageUrl(ImageType imageType, String imageUrl) { }

    public TagManager getTagManager()
    {
        return new TagManager(org);
    }

    public CatalogItemDao getDao()
    {
        return itemDao;
    }
}
