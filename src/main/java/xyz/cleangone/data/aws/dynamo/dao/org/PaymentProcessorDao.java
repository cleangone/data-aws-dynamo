package xyz.cleangone.data.aws.dynamo.dao.org;

import xyz.cleangone.data.aws.dynamo.dao.DynamoBaseDao;
import xyz.cleangone.data.aws.dynamo.entity.organization.PaymentProcessor;

public class PaymentProcessorDao extends DynamoBaseDao<PaymentProcessor>
{
    public PaymentProcessor getById(String id)
    {
        return mapper.load(PaymentProcessor.class, id);
    }
}



