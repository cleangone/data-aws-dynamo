package xyz.cleangone.data.aws.dynamo.dao;

import xyz.cleangone.data.aws.dynamo.entity.person.UserToken;

public class UserTokenDao extends DynamoBaseDao<UserToken>
{
    public UserToken getById(String id)
    {
        return mapper.load(UserToken.class, id);
    }
}



