package xyz.cleangone.data.aws.dynamo.dao;

import xyz.cleangone.data.aws.dynamo.entity.person.Address;

public class AddressDao extends CachingDao<Address>
{
    public Address getById(String id)
    {
        return mapper.load(Address.class, id);
    }
}



