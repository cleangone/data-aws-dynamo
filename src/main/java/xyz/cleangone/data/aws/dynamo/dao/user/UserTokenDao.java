package xyz.cleangone.data.aws.dynamo.dao.user;

import xyz.cleangone.data.aws.dynamo.dao.CachingDao;
import xyz.cleangone.data.aws.dynamo.entity.person.UserToken;

public class UserTokenDao extends CachingDao<UserToken>
{
    public UserToken getById(String id)
    {
        return mapper.load(UserToken.class, id);
    }
}



