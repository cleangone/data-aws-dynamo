package xyz.cleangone.data.aws.dynamo.entity.base;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "OrgLastTouched")
public class OrgLastTouched extends BaseEntity
{

// todo - move to EntityLastTouched if needed for multi-vm
    public OrgLastTouched() { }

    // orgLastTouched.id is the orgId
    public OrgLastTouched(String orgId)
    {
        setId(orgId);
        setUpdatedDate();
    }
}



