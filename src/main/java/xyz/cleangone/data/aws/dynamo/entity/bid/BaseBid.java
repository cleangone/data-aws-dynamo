package xyz.cleangone.data.aws.dynamo.entity.bid;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;

import java.math.BigDecimal;

@DynamoDBTable(tableName="BidDummy")
public class BaseBid extends BaseEntity
{
    protected String itemId;
    protected String userId;
    protected BigDecimal maxAmount;
    protected BigDecimal currAmount;
    protected boolean isHighBid;

    public BaseBid() {}
    public BaseBid(String itemId, String userId, BigDecimal maxAmount, BigDecimal currAmount, boolean isHighBid)
    {
        this.itemId = itemId;
        this.userId = userId;
        this.maxAmount = maxAmount;
        this.currAmount = currAmount;
        this.isHighBid = isHighBid;
    }

    @DynamoDBIgnore
    public boolean maxAmountGTE(BigDecimal amount)
    {
        return maxAmount.compareTo(amount) >= 0;
    }
    public boolean maxAmountEQ(BigDecimal amount)
    {
        return maxAmount.compareTo(amount) == 0;
    }
    public boolean currAmountGT(BigDecimal amount)
    {
        return currAmount.compareTo(amount) > 0;
    }

    @DynamoDBIgnore
    public String getDisplayMaxAmount()
    {
        return getDisplayAmount(maxAmount);
    }

    @DynamoDBIgnore
    public String getDisplayCurrAmount()
    {
        return getDisplayAmount(currAmount);
    }

    @DynamoDBIgnore
    private String getDisplayAmount(BigDecimal amount)
    {
        return amount == null ? "" : "$" + amount;
    }

    @DynamoDBAttribute(attributeName="ItemId")
    public String getItemId()
    {
        return itemId;
    }
    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }

    @DynamoDBAttribute(attributeName="UserId")
    public String getUserId()
    {
        return userId;
    }
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    @DynamoDBAttribute(attributeName="MaxAmount")
    public BigDecimal getMaxAmount()
    {
        return maxAmount;
    }
    public void setMaxAmount(BigDecimal maxAmount)
    {
        this.maxAmount = maxAmount;
    }

    @DynamoDBAttribute(attributeName="CurrAmount")
    public BigDecimal getCurrAmount()
    {
        return currAmount;
    }
    public void setCurrAmount(BigDecimal currAmount)
    {
        this.currAmount = currAmount;
    }

    @DynamoDBAttribute(attributeName="IsHighBid")
    public boolean getIsHighBid()
    {
        return isHighBid;
    }
    public void setIsHighBid(boolean highBid)
    {
        isHighBid = highBid;
    }
}


