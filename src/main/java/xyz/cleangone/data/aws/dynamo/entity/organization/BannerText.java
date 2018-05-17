package xyz.cleangone.data.aws.dynamo.entity.organization;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class BannerText
{
    private String html;
    private String size;
    private String lineHeight;   // %
    private String bottomOffset; // in pixels

    @DynamoDBAttribute(attributeName = "Html")
    public String getHtml()
    {
        return html;
    }
    public void setHtml(String html)
    {
        this.html = html;
    }

    @DynamoDBAttribute(attributeName = "Size")
    public String getSize()
    {
        return size;
    }
    public void setSize(String size)
    {
        this.size = size;
    }

    @DynamoDBAttribute(attributeName = "LineHeight")
    public String getLineHeight()
    {
        return lineHeight;
    }
    public void setLineHeight(String lineHeight)
    {
        this.lineHeight = lineHeight;
    }

    @DynamoDBAttribute(attributeName = "BottomOffset")
    public String getBottomOffset()
    {
        return bottomOffset;
    }
    public void setBottomOffset(String bottomOffset)
    {
        this.bottomOffset = bottomOffset;
    }
}
