package xyz.cleangone.data.aws.dynamo.entity.organization;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.util.Crypto;

import java.util.Objects;

@DynamoDBTable(tableName="PaymentProcessor")
public class PaymentProcessor extends BaseEntity
{
    // todo - should specific processor info be here?
    public enum PaymentProcessorType { iATS, None }

    public static final EntityField IATS_AGENT_CODE_FIELD = new EntityField("PaymentProcessor.iatsAgentCode", "iATS Agent Code");
    public static final EntityField IATS_PASSWORD_FIELD = new EntityField("PaymentProcessor.iatsPassord", "iATS Password");

    private PaymentProcessorType type;
    private String user;
    private String encryptedAuth;

    public PaymentProcessor()
    {
        super();
    }

    public String get(EntityField field)
    {
        if (IATS_AGENT_CODE_FIELD.equals(field)) return getUser();
        if (IATS_PASSWORD_FIELD.equals(field)) return "*";
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (IATS_AGENT_CODE_FIELD.equals(field)) setUser(value);
        else if (IATS_PASSWORD_FIELD.equals(field)) setAuth(value);
        else super.set(field, value);
    }

    public static boolean isValidIats(PaymentProcessor processor)
    {
        return (processor != null &&
            processor.isIats() &&
            processor.getUser() != null &&
            processor.getAuth() != null);
    }

    @DynamoDBIgnore public boolean isIats()
    {
        return type == PaymentProcessorType.iATS;
    }
    @DynamoDBIgnore public String getAuth()
    {
        return getEncryptedAuth() == null ? null : Crypto.decrypt(getEncryptedAuth());
    }
    @DynamoDBIgnore public void setAuth(String auth)
    {
        setEncryptedAuth(Crypto.encrypt(Objects.requireNonNull(auth)));
    }

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    @DynamoDBAttribute(attributeName = "Type")
    public PaymentProcessorType getType()
    {
        return type;
    }
    public void setType(PaymentProcessorType type)
    {
        this.type = type;
    }

    @DynamoDBAttribute(attributeName = "User")
    public String getUser()
    {
        return user;
    }
    public void setUser(String user)
    {
        this.user = user;
    }

    @DynamoDBAttribute(attributeName = "EncryptedAuth")
    public String getEncryptedAuth()
    {
        return encryptedAuth;
    }
    public void setEncryptedAuth(String encryptedAuth)
    {
        this.encryptedAuth = encryptedAuth;
    }
}


