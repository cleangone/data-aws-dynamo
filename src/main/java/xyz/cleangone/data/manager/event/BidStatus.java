package xyz.cleangone.data.manager.event;


import xyz.cleangone.data.aws.dynamo.entity.bid.ItemBid;
import xyz.cleangone.data.aws.dynamo.entity.bid.UserBid;

public class BidStatus
{
    UserBid userBid;
    ItemBid previousHighBid; // if a different user

    public BidStatus() { }
    public BidStatus(UserBid userBid)
    {
        this.userBid = userBid;
    }
    public BidStatus(UserBid userBid, ItemBid previousHighBid)
    {
        this.userBid = userBid;
        this.previousHighBid = previousHighBid;
    }

    public UserBid getUserBid()
    {
        return userBid;
    }

    public ItemBid getPreviousHighBid()
    {
        return previousHighBid;
    }
}
