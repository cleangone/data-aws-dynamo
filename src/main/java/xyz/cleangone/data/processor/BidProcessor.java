package xyz.cleangone.data.processor;

import xyz.cleangone.data.aws.dynamo.dao.CatalogItemDao;
import xyz.cleangone.data.aws.dynamo.dao.ItemBidDao;
import xyz.cleangone.data.aws.dynamo.dao.UserBidDao;
import xyz.cleangone.data.aws.dynamo.entity.bid.BidUtils;
import xyz.cleangone.data.aws.dynamo.entity.bid.ItemBid;
import xyz.cleangone.data.aws.dynamo.entity.bid.UserBid;
import xyz.cleangone.data.aws.dynamo.entity.item.CatalogItem;
import xyz.cleangone.data.manager.event.ItemManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class BidProcessor
{
    private final UserBidDao  userBidDao = new UserBidDao();
    private final ItemBidDao  itemBidDao = new ItemBidDao();
    private final ItemManager itemMgr = new ItemManager();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // first bid on item
    public void processFirstBid(UserBid userBid, CatalogItem item)
    {
        placeHighBid(userBid, item);
    }

    // current high bid will be increased, but stay as high bid
    public void incrementExistingHighBid(UserBid userBid, ItemBid currentHighBid, CatalogItem item)
    {
        // current item bid was outbid
        outbid(currentHighBid);

        // new bid generated an underbid at its max
        save(new ItemBid(userBid, userBid.getMaxAmount(), false));

        // current high bid is still high, one increment above new bid
        BigDecimal newBidAmount = currentHighBid.maxAmountEQ(userBid.getMaxAmount()) ?
            currentHighBid.getMaxAmount() : // bids were equal - original one wins
            BidUtils.getNewHighBidAmount(userBid.getMaxAmount(), currentHighBid.getMaxAmount());

        // increase high userBid's current amount
        UserBid currentHighUserBid = userBidDao.getById(currentHighBid.getUserBidId());
        currentHighUserBid.setCurrAmount(newBidAmount);
        save(currentHighUserBid);

        // current high userBid generates a new item high bid
        placeHighBid(currentHighUserBid, item);
    }

    // current user high bid was outbid
    public void processNewHighBid(UserBid userBid, ItemBid currentHighBid, CatalogItem item)
    {
        // current item bid was outbid
        outbid(currentHighBid);

        // previous high bidder bids its max unless already at max
        UserBid currentHighUserBid = userBidDao.getById(currentHighBid.getUserBidId());
        if (!currentHighUserBid.atMaxBid()) { save(new ItemBid(currentHighUserBid, currentHighBid.getMaxAmount(), false)); }
        outbid(currentHighUserBid);

        // new bid generated a new high item bid
        placeHighBid(userBid, item);
     }

    public void cleanupUserBids(List<UserBid> bids, UserBid highBid)
    {
        schedule(new UserBidFixer(bids, highBid, userBidDao));
    }
    public void cleanupHighBids(List<ItemBid> highBids, ItemBid highestBid)
    {
        schedule(new ItemBidFixer(highBids, highestBid, itemBidDao));
    }
    private void schedule(Runnable runnable)
    {
        scheduler.schedule(runnable, 1, SECONDS);
    }

    private void outbid(ItemBid bid)
    {
        bid.setIsHighBid(false);
        save(bid);
    }

    private void outbid(UserBid bid)
    {
        bid.setIsHighBid(false);
        bid.setCurrAmount(bid.getMaxAmount());
        save(bid);
    }

    private void placeHighBid(UserBid userBid, CatalogItem item)
    {
        ItemBid bid = save(new ItemBid(userBid, userBid.getCurrAmount(), true));

        //CatalogItem item = itemDao.getById(bid.getItemId());
        item.bid(bid);
        save(item);
    }

    private void save(UserBid userBid)
    {
        userBidDao.save(userBid);
    }

    private ItemBid save(ItemBid itemBid)
    {
        itemBidDao.save(itemBid);
        return itemBid;
    }

    private void save(CatalogItem item)
    {
        itemMgr.save(item); // also handles QueuedNotification
    }
}
