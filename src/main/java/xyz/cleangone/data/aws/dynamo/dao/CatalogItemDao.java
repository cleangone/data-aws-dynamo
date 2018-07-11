package xyz.cleangone.data.aws.dynamo.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.item.CatalogItem;

import java.util.List;

public class CatalogItemDao extends CachingDao<CatalogItem>
{
    public CatalogItem getById(String id)
    {
        return mapper.load(CatalogItem.class, id);
    }

    public List<CatalogItem> getByOrg(String eventId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("OrgId", eventId);
        return mapper.scan(CatalogItem.class, scanExpression);
    }

    public void save(CatalogItem item)
    {
        super.save(item);
        setEntityLastTouched(item.getOrgId(), EntityType.Item);
        setEntityLastTouched(item.getId(), EntityType.Bid);
    }

    public void delete(CatalogItem item)
    {
        setEntityLastTouched(item.getOrgId(), EntityType.Item);
        mapper.delete(item);
    }
}



