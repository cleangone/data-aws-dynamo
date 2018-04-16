package xyz.cleangone.data.aws.dynamo.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.bid.UserBid;

import java.util.List;

public class UserBidDao extends DynamoBaseDao<UserBid>
{
    public UserBid getById(String id)
    {
        return mapper.load(UserBid.class, id);
    }

    public List<UserBid> getByOrg(String orgId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("OrgId", orgId);
        return mapper.scan(UserBid.class, scanExpression);
    }

    public List<UserBid> getByItemId(String itemId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("ItemId", itemId);
        return mapper.scan(UserBid.class, scanExpression);
    }

    public List<UserBid> getByUserId(String userId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("UserId", userId);
        return mapper.scan(UserBid.class, scanExpression);
    }

    public void save(UserBid userBid)
    {
        super.save(userBid);
        entityLastTouchedCache.touch(userBid.getUserId(), EntityType.Bid);
        entityLastTouchedCache.touch(userBid.getItemId(), EntityType.Bid);
        setEntityLastTouched(userBid.getUserId(), EntityType.Bid);
        setEntityLastTouched(userBid.getItemId(), EntityType.Bid);
    }
}