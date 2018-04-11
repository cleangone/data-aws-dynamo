package xyz.cleangone.data.aws.dynamo.entity.bid;

import java.math.BigDecimal;

public class BidUtils
{
    public static BigDecimal getNewHighBidAmount(BigDecimal currBidAmount, BigDecimal maxAmount)
    {
        int increment = 1;
        if (currBidAmount.intValue() > 20)  { increment = 2; }
        if (currBidAmount.intValue() > 50)  { increment = 5; }
        if (currBidAmount.intValue() > 200) { increment = 10; }

        BigDecimal newBidAmount = currBidAmount.add(new BigDecimal(increment));
        return newBidAmount.compareTo(maxAmount) > 0 ? maxAmount : newBidAmount;
    }

}


