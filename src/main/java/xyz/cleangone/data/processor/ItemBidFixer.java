package xyz.cleangone.data.processor;

import xyz.cleangone.data.aws.dynamo.dao.ItemBidDao;
import xyz.cleangone.data.aws.dynamo.entity.bid.ItemBid;

import java.util.List;

public class ItemBidFixer implements Runnable
{
    private final List<ItemBid> highBids;
    private final ItemBid highestBid;
    private final ItemBidDao itemBidDao;

    public ItemBidFixer(List<ItemBid> highBids, ItemBid highestBid, ItemBidDao itemBidDao)
    {
        this.highBids = highBids;
        this.highestBid = highestBid;
        this.itemBidDao = itemBidDao;
    }

    public void run()
    {
        System.out.println("Fixing multiple highBid ItemBids on Item " + highestBid.getItemId());
        for (ItemBid highBid : highBids)
        {
            if (!highBid.getId().equals(highestBid.getId()) &&
                highBid.getIsHighBid())
            {
                highBid.setIsHighBid(false);
                itemBidDao.save(highBid);
            }
        }
    }

}
