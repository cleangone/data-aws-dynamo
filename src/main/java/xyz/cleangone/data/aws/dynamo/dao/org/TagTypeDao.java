package xyz.cleangone.data.aws.dynamo.dao.org;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.dao.BaseOrgDao;
import xyz.cleangone.data.aws.dynamo.entity.organization.*;

import java.util.List;

import static xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag.ORG_ID_ATTRIBUTE;

public class TagTypeDao extends BaseOrgDao<TagType>
{
    public List<TagType> getByOrg(String orgId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression(ORG_ID_ATTRIBUTE, orgId);
        return mapper.scan(TagType.class, scanExpression);
    }
}



