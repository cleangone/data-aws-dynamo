package xyz.cleangone.data.aws.dynamo.dao.org;

import xyz.cleangone.data.aws.dynamo.dao.CachingDao;
import xyz.cleangone.data.aws.dynamo.entity.organization.PaymentProcessor;

public class PaymentProcessorDao extends CachingDao<PaymentProcessor>
{
    public PaymentProcessor getById(String id)
    {
        return mapper.load(PaymentProcessor.class, id);
    }
}



