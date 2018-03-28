package xyz.cleangone.data.aws.dynamo.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.entity.item.CatalogItem;

import java.util.List;

public class CatalogItemDao extends DynamoBaseDao<CatalogItem>
{
    public List<CatalogItem> getByOrg(String eventId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("OrgId", eventId);
        return mapper.scan(CatalogItem.class, scanExpression);
    }

    public void save(CatalogItem item)
    {
        super.save(item);
        saveLastTouch(item.getOrgId());
    }

    public void delete(CatalogItem item)
    {
        saveLastTouch(item.getOrgId());
        mapper.delete(item);
    }
}



