package xyz.cleangone.data.aws.dynamo.entity.action;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.organization.EventParticipant;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgEvent;
import xyz.cleangone.data.aws.dynamo.entity.person.Person;
import xyz.cleangone.data.aws.dynamo.entity.person.User;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.util.Objects.requireNonNull;

@DynamoDBTable(tableName="Action")
public class Action extends BaseEntity
{
    public static final EntityField AMOUNT_FIELD = new EntityField("action.amount", "Amount");
    public static final EntityField ESTIMATED_AMOUNT_FIELD = new EntityField("action.estimatedAmount", "Amount (Est)");

    private String orgId;
    private ActionType actionType;
    private BigDecimal amount;
    private BigDecimal iterationAmount;
    private String description;
    private String sourcePersonId;  // todo - interesting that this is not a userId
    private String targetEventId;
    private String targetPersonId;
    private String referenceActionId;

    // the following are held redundantly in case the entities are deleted
    private String sourcePersonLastCommaFirst;
    private String targetEventName;
    private String targetPersonFirstLast;


    public Action()
    {
        super();
    }

    public Action(String orgId, ActionType actionType)
    {
        super();
        setOrgId(requireNonNull(orgId));
        setActionType(requireNonNull(actionType));
    }

    public Action(String orgId, ActionType actionType, OrgEvent event)
    {
        this(orgId, actionType);
        setTargetEvent(requireNonNull(event));
    }

    public Action(String orgId, ActionType actionType, OrgEvent event, User user)
    {
        this(orgId, actionType, event);
        withSourcePerson(requireNonNull(user));
    }

    public Action(String orgId, ActionType actionType, OrgEvent event, User user, BigDecimal amount)
    {
        this(orgId, actionType, event, user);
        setAmount(requireNonNull(amount));
    }

    public Action withSourcePerson(User user)
    {
        if (user == null) { return this; }

        setSourcePersonId(user.getId()); // null for on-the-fly users
        setSourcePersonLastCommaFirst(user.getLastCommaFirst());
        return this;
    }

    public Action withTargetPerson(EventParticipant participant)
    {
        if (participant == null) { return this; }

        setTargetPerson(participant.getPerson());
        return this;
    }

    @DynamoDBIgnore
    private void setTargetEvent(OrgEvent event)
    {
        targetEventId = event.getId();
        targetEventName = event.getName();
    }

    @DynamoDBIgnore
    private void setTargetPerson(Person person)
    {
        targetPersonId = person.getId();
        targetPersonFirstLast = person.getFirstLast();
    }

    @DynamoDBIgnore
    public String getDisplayAmount()
    {
        return getDisplayAmount(amount);
    }
    public static String getDisplayAmount(BigDecimal amount)
    {
        return amount == null ? "" : "$" + amount.setScale(2, RoundingMode.CEILING);
    }

    @DynamoDBIgnore
    public BigDecimal getEstimatedAmount(int estimatedIterations)
    {
        if (actionType == ActionType.Donated || actionType == ActionType.FulfilledPledge)
        {
            return amount;
        }
        else if (actionType == ActionType.Pledged)
        {
            return iterationAmount == null ? null : iterationAmount.multiply(new BigDecimal(estimatedIterations));
        }

        return null;
    }

    @DynamoDBAttribute(attributeName = "OrgId")
    public String getOrgId()
    {
        return orgId;
    }
    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName="ActionType")
    public ActionType getActionType()
    {
        return actionType;
    }
    public void setActionType(ActionType actionType)
    {
        this.actionType = actionType;
    }

    @DynamoDBAttribute(attributeName = "Amount")
    public BigDecimal getAmount()
    {
        return amount;
    }
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }
    public boolean hasAmount()
    {
        return amount != null;
    }

    @DynamoDBAttribute(attributeName = "IterationAmount")
    public BigDecimal getIterationAmount()
    {
        return iterationAmount;
    }
    public void setIterationAmount(BigDecimal iterationAmount)
    {
        this.iterationAmount = iterationAmount;
    }
    public Action withIterationAmount(BigDecimal amount)
    {
        setIterationAmount(requireNonNull(amount));
        return this;
    }
    public boolean hasIterationAmount()
    {
        return iterationAmount != null;
    }


    @DynamoDBAttribute(attributeName = "Description")
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public Action withDescription(String description)
    {
        setDescription(description);
        return this;
    }

    @DynamoDBAttribute(attributeName = "SourcePersonId")
    public String getSourcePersonId()
    {
        return sourcePersonId;
    }
    public void setSourcePersonId(String sourcePersonId)
    {
        this.sourcePersonId = sourcePersonId;
    }


    @DynamoDBAttribute(attributeName = "SourcePersonLastCommaFirst")
    public String getSourcePersonLastCommaFirst()
    {
        return sourcePersonLastCommaFirst;
    }
    public void setSourcePersonLastCommaFirst(String lastCommaFirst)
    {
        sourcePersonLastCommaFirst = lastCommaFirst;
    }

    @DynamoDBAttribute(attributeName = "TargetEventId")
    public String getTargetEventId()
    {
        return targetEventId;
    }
    public void setTargetEventId(String targetEventId)
    {
        this.targetEventId = targetEventId;
    }

    @DynamoDBAttribute(attributeName = "TargetEventName")
    public String getTargetEventName()
    {
        return targetEventName;
    }
    public void setTargetEventName(String targetEventName)
    {
        this.targetEventName = targetEventName;
    }

    @DynamoDBAttribute(attributeName = "TargetPersonId")
    public String getTargetPersonId()
    {
        return targetPersonId;
    }
    public void setTargetPersonId(String targetPersonId)
    {
        this.targetPersonId = targetPersonId;
    }
    public Action withTargetPersonId(String personId)
    {
        setTargetPersonId(requireNonNull(personId));
        return this;
    }

    @DynamoDBAttribute(attributeName = "TargetPersonFirstLast")
    public String getTargetPersonFirstLast()
    {
        return targetPersonFirstLast;
    }
    public void setTargetPersonFirstLast(String targetPersonFirstLast) { this.targetPersonFirstLast = targetPersonFirstLast; }

    @DynamoDBAttribute(attributeName = "ReferenceActionId")
    public String getReferenceActionId()
    {
        return referenceActionId;
    }
    public void setReferenceActionId(String referenceActionId)
    {
        this.referenceActionId = referenceActionId;
    }
    public Action withReferenceAction(Action action)
    {
        setReferenceActionId(action.getId());
        return this;
    }

}


