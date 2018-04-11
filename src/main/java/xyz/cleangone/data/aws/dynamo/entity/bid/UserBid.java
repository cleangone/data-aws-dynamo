package xyz.cleangone.data.aws.dynamo.entity.bid;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import xyz.cleangone.data.aws.dynamo.entity.item.CatalogItem;
import xyz.cleangone.data.aws.dynamo.entity.person.User;

import java.math.BigDecimal;

@DynamoDBTable(tableName="UserBid")
public class UserBid extends BaseBid
{
    public UserBid() {}
    public UserBid(CatalogItem item, User user, BigDecimal maxAmount, BigDecimal currAmount, boolean isHighBid)
    {
        super(item.getId(), user.getId(), maxAmount, currAmount, isHighBid);
    }

}


