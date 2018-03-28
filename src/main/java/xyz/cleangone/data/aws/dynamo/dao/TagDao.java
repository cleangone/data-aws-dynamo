package xyz.cleangone.data.aws.dynamo.dao;

import static xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag.*;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;
import xyz.cleangone.data.cache.EntityLastTouched;
import xyz.cleangone.data.cache.EntityType;

import java.util.List;

public class TagDao extends DynamoBaseDao<OrgTag>
{
    public List<OrgTag> getByOrg(String orgId)
    {
        DynamoDBScanExpression scanExpression = buildEqualsScanExpression(ORG_ID_ATTRIBUTE, orgId);
        return mapper.scan(OrgTag.class, scanExpression);
    }

    public void save(OrgTag tag)
    {
        super.save(tag);

        String entityId = tag.getEventId() == null ? tag.getOrgId() : tag.getEventId();
        if (tag.isTagType(TagType.PersonTag)) { entityLastTouched.touch(entityId, EntityType.PersonTag); }
        else if (tag.isTagType(TagType.Category)) { entityLastTouched.touch(entityId, EntityType.Category); }

        saveLastTouch(tag.getOrgId());
    }
}



