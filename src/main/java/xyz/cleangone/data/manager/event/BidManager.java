package xyz.cleangone.data.manager.event;

import xyz.cleangone.data.aws.dynamo.dao.ItemBidDao;
import xyz.cleangone.data.aws.dynamo.dao.UserBidDao;
import xyz.cleangone.data.aws.dynamo.entity.bid.BidUtils;
import xyz.cleangone.data.aws.dynamo.entity.bid.ItemBid;
import xyz.cleangone.data.aws.dynamo.entity.item.CatalogItem;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.aws.dynamo.entity.bid.UserBid;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


public class BidManager
{
    private final UserBidDao userBidDao = new UserBidDao();
    private final ItemBidDao itemBidDao = new ItemBidDao();
    private final BidEngine  bidEngine  = new BidEngine();

    public ItemBid getItemBid(String itemBidId)
    {
        return itemBidDao.getById(itemBidId);
    }

    public UserBid createBid(User user, CatalogItem item, BigDecimal maxAmount)
    {
        if (maxAmount.compareTo(item.getPrice()) < 0) { return null; }

        ItemBid currentHighBid = getHighBid(item);
        if (currentHighBid == null)
        {
            // first bid - create a high bid at the current price
            BigDecimal bidAmount = item.getPrice();
            UserBid userBid = save(new UserBid(item, user, maxAmount, bidAmount, true));
            bidEngine.processFirstBid(userBid.getId());

            return userBid;
        }

        // check if user has a previous bid on this item (could be high bid or outbid)
        UserBid userBid = getUserBid(user, item);
        if (userBid != null)
        {
            if (userBid.maxAmountGTE(maxAmount)) { return userBid; }  // bid was less than previous - no-op

            // increase max bid
            userBid.setMaxAmount(maxAmount);

            if (currentHighBid.getUserBidId().equals(userBid.getId()))
            {
                // user already has high bid - just increase its max
                save(userBid);
                currentHighBid.setMaxAmount(maxAmount);
                save(currentHighBid);
            }
            else if (currentHighBid.maxAmountGTE(maxAmount))
            {
                // user was not high bidder, and is still not, but may have pushed up bid
                userBid.setCurrAmount(maxAmount);
                save(userBid);
                bidEngine.incrementExistingHighBid(userBid.getId(), currentHighBid.getId());
            }
            else
            {
                // update bid is now the highest - one increment above existing highBid
                BigDecimal bidAmount = BidUtils.getNewHighBidAmount(currentHighBid.getMaxAmount(), maxAmount);
                userBid.setCurrAmount(bidAmount);
                save(userBid);
                bidEngine.processNewHighBid(userBid.getId(), currentHighBid.getId());
            }

            return userBid;
        }

        // user does not have a previous bid on this item
        if (currentHighBid.maxAmountGTE(maxAmount))
        {
            // current high bid will be increased, but stay as high bid

            // return a userBid that is not the high bid
            userBid = save(new UserBid(item, user, maxAmount, maxAmount, false));
            bidEngine.incrementExistingHighBid(userBid.getId(), currentHighBid.getId());
        }
        else
        {
            // new bid is the highest - one increment above existing highBid
            BigDecimal bidAmount = BidUtils.getNewHighBidAmount(currentHighBid.getMaxAmount(), maxAmount);
            userBid = save(new UserBid(item, user, maxAmount, bidAmount, true));
            bidEngine.processNewHighBid(userBid.getId(), currentHighBid.getId());
        }

        return userBid;
    }

    private UserBid getUserBid(User user, CatalogItem item)
    {
        List<UserBid> userBids = userBidDao.getByItemId(item.getId()).stream()
            .filter(b -> b.getUserId().equals(user.getId()))
            .collect(Collectors.toList());

        if (userBids.size() == 0) { return null; }
        else if (userBids.size() == 1) { return userBids.get(0); }
        else { return getHighBid(userBids); } // todo - error - should be fixed by bidEngine
    }

    private ItemBid getHighBid(CatalogItem item)
    {
        // should be only one, but be safe
        List<ItemBid> itemBids = itemBidDao.getByItemId(item.getId());
        List<ItemBid> highBids = itemBids.stream()
            .filter(ItemBid::getIsHighBid)
            .collect(Collectors.toList());
        if (highBids.size() == 0) { return null; }
        else if (highBids.size() == 1) { return highBids.get(0); }

        ItemBid highestBid = null;
        for (ItemBid bid : highBids)
        {
            if (highestBid == null || bid.currAmountGT(highestBid.getCurrAmount())) { highestBid = bid; }
        }

        return highestBid;
    }

    private UserBid getHighBid(List<UserBid> userBids)
    {
        // should only be one - error condition will be fixed by bidEngine
        UserBid highBid = null;
        for (UserBid bid : userBids)
        {
            if (highBid == null || bid.maxAmountGTE(highBid.getMaxAmount())) { highBid = bid; }
        }

        return highBid;
    }

    private UserBid save(UserBid userBid)
    {
        userBidDao.save(userBid);
        return userBid;
    }

    private void save(ItemBid itemBid)
    {
        itemBidDao.save(itemBid);
    }
}
