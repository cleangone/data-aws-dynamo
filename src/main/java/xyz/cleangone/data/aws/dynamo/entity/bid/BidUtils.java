package xyz.cleangone.data.aws.dynamo.entity.bid;

import java.math.BigDecimal;

public class BidUtils
{
    public static BigDecimal getNewHighBidAmount(BigDecimal currBidMaxAmount, BigDecimal newBidMaxAmount)
    {
        BigDecimal newBidAmount = getIncrementedAmount(currBidMaxAmount);
        return newBidAmount.compareTo(newBidMaxAmount) > 0 ? newBidMaxAmount : newBidAmount;
    }

    public static BigDecimal getIncrementedAmount(BigDecimal amount)
    {
        int increment = 1;
        if (amount.intValue() > 20)  { increment = 2; }
        if (amount.intValue() > 50)  { increment = 5; }
        if (amount.intValue() > 200) { increment = 10; }
        if (amount.intValue() > 400) { increment = 20; }
        if (amount.intValue() > 500) { increment = 25; }

        return amount.add(new BigDecimal(increment));
    }
}