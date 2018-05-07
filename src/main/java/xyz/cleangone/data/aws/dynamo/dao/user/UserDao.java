package xyz.cleangone.data.aws.dynamo.dao.user;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.dao.DynamoBaseDao;
import xyz.cleangone.data.aws.dynamo.entity.person.Person;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;

import java.util.List;

public class UserDao extends DynamoBaseDao<User>
{
    public User getById(String id)
    {
        return mapper.load(User.class, id);
    }

    public List<User> getByOrg(String orgId)
    {
        DynamoDBScanExpression scanExpression = buildContainsScanExpression("OrgIds", orgId);
        return mapper.scan(User.class, scanExpression);
    }

    public List<User> getByEmail(String email)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("Email", email);
        return mapper.scan(User.class, scanExpression);
    }

    public void save(User user)
    {
        super.save(user);
        entityLastTouchedCache.touch(user.getId(), EntityType.Entity);
//        entityLastTouchedCache.touch(user.getOrgId(), EntityType.User);
//        setEntityLastTouched(user.getOrgId(), EntityType.User);
    }
}



