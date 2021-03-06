package xyz.cleangone.data.aws.dynamo.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.entity.person.Person;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;

import java.util.List;

public class PersonDao extends BaseOrgDao<Person>
{
    public Person getById(String id)
    {
        return mapper.load(Person.class, id);
    }

    public List<Person> getByOrg(String orgId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression("OrgId", orgId);
        return mapper.scan(Person.class, scanExpression);
    }

    public List<Person> getByTag(String tagId)
    {
        DynamoDBScanExpression scanExpression = buildContainsScanExpression("TagIds", tagId);
        return mapper.scan(Person.class, scanExpression);
    }

    public void save(Person person)
    {
        super.save(person);
        entityLastTouchedCache.touch(person.getId(), EntityType.ENTITY);
        entityLastTouchedCache.touch(person.getOrgId(), EntityType.PERSON);
        setEntityLastTouched(person.getOrgId(), EntityType.PERSON);
    }
}
