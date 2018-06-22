package xyz.cleangone.data.aws.dynamo.dao.org;

import static xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag.*;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import xyz.cleangone.data.aws.dynamo.dao.DynamoBaseDao;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.organization.TagType;

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
        setEntityLastTouched(tag.getOrgId(), EntityType.Tag);

        // the tag either applies to the org, or an event
        String entityId = tag.getEventId() == null ? tag.getOrgId() : tag.getEventId();
        if (tag.isTagType(TagType.PERSON_TAG_TAG_TYPE))
        {
            entityLastTouchedCache.touch(entityId, EntityType.PersonTag);
        }
        else if (tag.isTagType(TagType.CATEGORY_TAG_TYPE))
        {
            entityLastTouchedCache.touch(entityId, EntityType.Category);
        }
    }

    public void delete(OrgTag tag)
    {
        super.delete(tag);
        setEntityLastTouched(tag.getOrgId(), EntityType.Tag);
    }
}



