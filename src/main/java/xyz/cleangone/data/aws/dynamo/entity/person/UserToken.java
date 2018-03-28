package xyz.cleangone.data.aws.dynamo.entity.person;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;

@DynamoDBTable(tableName="UserToken")
public class UserToken extends BaseEntity
{
    private String userId;

    public UserToken()
    {
        super();
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

}


