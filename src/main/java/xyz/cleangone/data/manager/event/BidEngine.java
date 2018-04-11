package xyz.cleangone.data.manager.event;

import xyz.cleangone.data.aws.dynamo.dao.CatalogItemDao;
import xyz.cleangone.data.aws.dynamo.dao.ItemBidDao;
import xyz.cleangone.data.aws.dynamo.dao.UserBidDao;
import xyz.cleangone.data.aws.dynamo.entity.bid.BidUtils;
import xyz.cleangone.data.aws.dynamo.entity.bid.ItemBid;
import xyz.cleangone.data.aws.dynamo.entity.bid.UserBid;
import xyz.cleangone.data.aws.dynamo.entity.item.CatalogItem;
import xyz.cleangone.data.aws.dynamo.entity.item.PurchaseItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


// to be moved to async lambda
public class BidEngine
{
    private final UserBidDao  userBidDao = new UserBidDao();
    private final ItemBidDao  itemBidDao = new ItemBidDao();
    private final CatalogItemDao itemDao = new CatalogItemDao();

    // first bid on item
    public void processFirstBid(String userBidId)
    {
        UserBid userBid = userBidDao.getById(userBidId);
        highBid(userBid);
    }

    // current high bid will be increased, but stay as high bid
    public void incrementExistingHighBid(String userBidId, String currentHighItemBidId)
    {
        UserBid userBid = userBidDao.getById(userBidId);
        ItemBid currentHighBid = itemBidDao.getById(currentHighItemBidId);

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
        highBid(currentHighUserBid);
    }

    // current user high bid was outbid
    public void processNewHighBid(String userBidId, String currentHighItemBidId)
    {
        UserBid userBid = userBidDao.getById(userBidId);
        ItemBid currentHighBid = itemBidDao.getById(currentHighItemBidId);

        // current item bid was outbid
        outbid(currentHighBid);

        // previous high bidder bids its max
        UserBid currentHighUserBid = userBidDao.getById(currentHighBid.getUserBidId());
        save(new ItemBid(currentHighUserBid, currentHighBid.getMaxAmount(), false));
        outbid(currentHighUserBid);

        // new bid generated a new high item bid
        highBid(userBid);
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

    private void highBid(UserBid userBid)
    {
        ItemBid bid = save(new ItemBid(userBid, userBid.getCurrAmount(), true));

        CatalogItem item = itemDao.getById(bid.getItemId());
        item.setPrice(bid.getCurrAmount());
        item.setHighBidId(bid.getId());
        save(item);
    }


    // todo - do cleanup in bidEngine
    private ItemBid getHighBid(PurchaseItem item)
    {
        // should be only one, but be safe
        List<ItemBid> itemBids = itemBidDao.getByItemId(item.getId());
        List<ItemBid> highBids = itemBids.stream()
            .filter(ItemBid::getIsHighBid)
            .collect(Collectors.toList());
        if (highBids.size() == 0) { return null; }
        else if (highBids.size() == 1) { return highBids.get(0); }


        // should only be one - fix error condition
        ItemBid highestBid = null;
        for (ItemBid bid : highBids)
        {
            if (highestBid == null || bid.getCurrAmount().compareTo(highestBid.getCurrAmount()) > 0)
            {
                highestBid = bid;
            }
        }

        for (ItemBid bid : highBids)
        {
            if (bid != highestBid)
            {
                // todo - need to also look up userBid
                bid.setIsHighBid(false);
                save(bid);
            }
        }

        return highestBid;
    }

    private UserBid save(UserBid userBid)
    {
        userBidDao.save(userBid);
        return userBid;
    }

    private ItemBid save(ItemBid itemBid)
    {
        itemBidDao.save(itemBid);
        return itemBid;
    }
    private void save(CatalogItem item)
    {
        itemDao.save(item);
    }
}
