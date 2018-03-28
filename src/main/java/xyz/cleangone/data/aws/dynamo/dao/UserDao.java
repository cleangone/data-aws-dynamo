package xyz.cleangone.data.aws.dynamo.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import java.util.List;

public class UserDao extends DynamoBaseDao<User>
{
    public User getById(String id)
    {
        return mapper.load(User.class, id);
    }

    public List<User> getByOrg(String orgId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("OrgId", orgId);
        return mapper.scan(User.class, scanExpression);
    }

    public List<User> getByEmail(String email)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("Email", email);
        return mapper.scan(User.class, scanExpression);
    }
}



