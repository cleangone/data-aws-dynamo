package xyz.cleangone.data.aws.dynamo.dao.org;

import xyz.cleangone.data.aws.dynamo.dao.BaseOrgDao;
import xyz.cleangone.data.aws.dynamo.entity.organization.PaymentProcessor;

public class PaymentProcessorDao extends BaseOrgDao<PaymentProcessor>
{
    public PaymentProcessor getById(String id)
    {
        return mapper.load(PaymentProcessor.class, id);
    }
}



