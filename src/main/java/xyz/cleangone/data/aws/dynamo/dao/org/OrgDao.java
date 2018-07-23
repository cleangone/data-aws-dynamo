package xyz.cleangone.data.aws.dynamo.dao.org;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.dao.BaseOrgDao;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;

import java.util.List;

public class OrgDao extends BaseOrgDao<Organization>
{
    public Organization getById(String id)
    {
        return mapper.load(Organization.class, id);
    }

    public List<Organization> getAll()
    {
        return mapper.scan(Organization.class, new DynamoDBScanExpression());
    }

    // todo - scan & filter
    // todo - need a base thing to transform fm lists that expects one item
    public Organization get(String name)
    {
        List<Organization> orgs = getAll();
        for (Organization org : orgs)
        {
            if (org.getName().equals(name)) { return org; }
        }

        return null;
    }

    public Organization getByTag(String tag)
    {
        List<Organization> orgs = mapper.scan(Organization.class, byTag(tag));

        return orgs.isEmpty() ? null : orgs.get(0);
    }

    public void save(Organization org)
    {
        super.save(org);
        entityLastTouchedCache.touch(org.getId(), EntityType.ENTITY);
        setEntityLastTouched(org.getId(), EntityType.ENTITY);
    }
}
