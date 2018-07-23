package xyz.cleangone.data.aws.dynamo.dao.user;

import xyz.cleangone.data.aws.dynamo.dao.BaseOrgDao;
import xyz.cleangone.data.aws.dynamo.entity.person.UserToken;

public class UserTokenDao extends BaseOrgDao<UserToken>
{
    public UserToken getById(String id)
    {
        return mapper.load(UserToken.class, id);
    }
}



