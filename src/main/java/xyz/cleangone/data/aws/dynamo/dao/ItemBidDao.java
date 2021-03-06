package xyz.cleangone.data.aws.dynamo.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.bid.ItemBid;

import java.util.List;

public class ItemBidDao extends BaseOrgDao<ItemBid>
{
    public ItemBid getById(String id)
    {
        return mapper.load(ItemBid.class, id);
    }

    public List<ItemBid> getByItemId(String itemId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("ItemId", itemId);
        return mapper.scan(ItemBid.class, scanExpression);
    }

    public void save(ItemBid itemBid)
    {
        super.save(itemBid);
        entityLastTouchedCache.touch(itemBid.getItemId(), EntityType.BID);
        setEntityLastTouched(itemBid.getItemId(), EntityType.BID);
    }
}



