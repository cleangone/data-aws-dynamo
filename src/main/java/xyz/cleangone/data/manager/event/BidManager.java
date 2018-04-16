package xyz.cleangone.data.manager.event;

import xyz.cleangone.data.aws.dynamo.dao.ItemBidDao;
import xyz.cleangone.data.aws.dynamo.dao.UserBidDao;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.bid.BidUtils;
import xyz.cleangone.data.aws.dynamo.entity.bid.ItemBid;
import xyz.cleangone.data.aws.dynamo.entity.item.CatalogItem;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.aws.dynamo.entity.bid.UserBid;
import xyz.cleangone.data.cache.EntityCache;
import xyz.cleangone.data.processor.BidProcessor;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class BidManager
{
    // bids cached by item
    public static final EntityCache<ItemBid> ITEM_BID_CACHE = new EntityCache<>(EntityType.Bid);

    private final Organization org;

    private final UserBidDao userBidDao = new UserBidDao();
    private final ItemBidDao itemBidDao = new ItemBidDao();
    private final BidProcessor bidProcessor  = new BidProcessor();

    public BidManager(Organization org)
    {
        this.org = requireNonNull(org);
    }

    public ItemBid getItemBid(String itemBidId)
    {
        return itemBidDao.getById(itemBidId);
    }

    public List<UserBid> getUserBids(User user)
    {
        List<UserBid> bids = new ArrayList<>(userBidDao.getByUserId(user.getId()));
        bids.sort((it1, it2) -> it1.getCreatedDate().before(it2.getCreatedDate()) ? 1 : -1); // sort latest first

        return bids;
    }

    public List<ItemBid> getItemBids(CatalogItem item)
    {
        Date start = new Date();
        List<ItemBid> itemBids = ITEM_BID_CACHE.get(item, org.getId());
        if (itemBids != null) { return itemBids; }

        itemBids = new ArrayList<>(itemBidDao.getByItemId(item.getId()));
        itemBids.sort((it1, it2) -> it1.getCreatedDate().before(it2.getCreatedDate()) ? -1 : 1);
        ITEM_BID_CACHE.put(item, itemBids, org.getId(), start);

        return itemBids;
    }

    // todo - the specified item will be changed - this is either a good shortcut or bad programming by side-effect
    public BidStatus createBid(User user, CatalogItem item, BigDecimal maxAmount)
    {
        if (maxAmount.compareTo(item.getPrice()) < 0) { return new BidStatus(); }

        ItemBid currentHighBid = getHighBid(item);
        if (currentHighBid == null)
        {
            // first bid - create a high bid at the current price
            BigDecimal bidAmount = item.getPrice();
            UserBid userBid = save(new UserBid(item, user, maxAmount, bidAmount, true));
            bidProcessor.processFirstBid(userBid, item);

            return new BidStatus(userBid);
        }

        // check if user has a previous bid on this item (could be high bid or outbid)
        UserBid existingUserBid = getUserBid(user, item);
        if (existingUserBid != null)
        {
            if (existingUserBid.maxAmountGTE(maxAmount)) { return new BidStatus(); }  // bid was less than previous - no-op

            // increase max bid
            existingUserBid.setMaxAmount(maxAmount);

            if (currentHighBid.getUserBidId().equals(existingUserBid.getId()))
            {
                // user already has high bid - just increase its max
                save(existingUserBid);
                currentHighBid.setMaxAmount(maxAmount);
                save(currentHighBid);
                return new BidStatus(existingUserBid);
            }

            if (currentHighBid.maxAmountGTE(maxAmount))
            {
                // user was not high bidder, and is still not, but may have pushed up bid
                existingUserBid.setCurrAmount(maxAmount);
                save(existingUserBid);
                bidProcessor.incrementExistingHighBid(existingUserBid, currentHighBid, item);
                return new BidStatus(existingUserBid);
            }

            // updated bid is now the highest - one increment above existing highBid
            BigDecimal bidAmount = BidUtils.getNewHighBidAmount(currentHighBid.getMaxAmount(), maxAmount);
            existingUserBid.setCurrAmount(bidAmount);
            existingUserBid.setIsHighBid(true);
            save(existingUserBid);
            bidProcessor.processNewHighBid(existingUserBid, currentHighBid, item);
            return new BidStatus(existingUserBid, currentHighBid);
        }

        if (currentHighBid.maxAmountGTE(maxAmount))
        {
            // current high bid will be increased, but stay as high bid
            // return a userBid at its max that is not the high bid
            UserBid userBid = save(new UserBid(item, user, maxAmount, maxAmount, false));
            bidProcessor.incrementExistingHighBid(userBid, currentHighBid, item);
            return new BidStatus(userBid);
        }

        // new bid is the highest - one increment above existing highBid
        BigDecimal bidAmount = BidUtils.getNewHighBidAmount(currentHighBid.getMaxAmount(), maxAmount);
        UserBid userBid = save(new UserBid(item, user, maxAmount, bidAmount, true));
        bidProcessor.processNewHighBid(userBid, currentHighBid, item);
        return new BidStatus(userBid, currentHighBid);
    }

    private UserBid getUserBid(User user, CatalogItem item)
    {
        List<UserBid> userBids = userBidDao.getByItemId(item.getId()).stream()
            .filter(b -> b.getUserId().equals(user.getId()))
            .collect(Collectors.toList());

        if (userBids.size() == 0) { return null; }
        else if (userBids.size() == 1) { return userBids.get(0); }

        // should only be one userBid per item
        UserBid highBid = null;
        for (UserBid bid : userBids)
        {
            if (highBid == null || bid.maxAmountGTE(highBid.getMaxAmount())) { highBid = bid; }
        }

        bidProcessor.cleanupUserBids(userBids, highBid);

       return highBid;
    }

    private ItemBid getHighBid(CatalogItem item)
    {
        List<ItemBid> itemBids = itemBidDao.getByItemId(item.getId());
        List<ItemBid> highBids = itemBids.stream()
            .filter(ItemBid::getIsHighBid)
            .collect(Collectors.toList());

        if (highBids.size() == 0) { return null; }
        else if (highBids.size() == 1) { return highBids.get(0); }

        // should be only one highBid
        ItemBid highestBid = null;
        for (ItemBid bid : highBids)
        {
            if (highestBid == null || bid.currAmountGT(highestBid.getCurrAmount())) { highestBid = bid; }
        }

        bidProcessor.cleanupHighBids(highBids, highestBid);

        return highestBid;
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
