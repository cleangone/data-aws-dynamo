package xyz.cleangone.data.aws.dynamo.entity.bid;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.math.BigDecimal;

@DynamoDBTable(tableName="ItemBid")
public class ItemBid extends BaseBid
{
    private String userBidId;  // user may delete userBid if it is not the high bid

    public ItemBid() {}
    public ItemBid(UserBid userBid, BigDecimal currAmount, boolean isHighBid)
    {
        super(userBid.getItemId(), userBid.getUserId(), userBid.getMaxAmount(), currAmount, isHighBid);
        this.userBidId = userBid.getId();
    }

    @DynamoDBAttribute(attributeName="UserBidId")
    public String getUserBidId()
    {
        return userBidId;
    }
    public void setUserBidId(String userBidId)
    {
        this.userBidId = userBidId;
    }
}


