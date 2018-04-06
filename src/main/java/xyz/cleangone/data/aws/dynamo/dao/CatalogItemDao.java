package xyz.cleangone.data.aws.dynamo.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.base.OrgLastTouched;
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
        setEntityLastTouched(item.getOrgId(), EntityType.Item);
    }

    public void delete(CatalogItem item)
    {
        setEntityLastTouched(item.getOrgId(), EntityType.Item);
        mapper.delete(item);
    }
}



