package xyz.cleangone.data.processor;

import xyz.cleangone.data.aws.dynamo.dao.UserBidDao;
import xyz.cleangone.data.aws.dynamo.entity.bid.UserBid;

import java.util.List;

public class UserBidFixer implements Runnable
{
    private final List<UserBid> userBids;
    private final UserBid highBid;
    private final UserBidDao userBidDao;

    public UserBidFixer(List<UserBid> userBids, UserBid highBid, UserBidDao userBidDao)
    {
        this.userBids = userBids;
        this.highBid = highBid;
        this.userBidDao = userBidDao;
    }

    public void run()
    {
        System.out.println("Fixing multiple UserBids by User " + highBid.getUserId() + " for item " + highBid.getItemId());
        for (UserBid userBid : userBids)
        {
            if (!userBid.getId().equals(highBid.getId()) &&
                userBid.getUserId().equals(highBid.getUserId()) &&
                userBid.getItemId().equals(highBid.getItemId()))
            {
                userBidDao.delete(userBid);
            }
        }
    }
}
