package xyz.cleangone.data.aws.dynamo.entity.person;

import static java.util.Objects.requireNonNull;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;

@DynamoDBDocument
public class UserPrivledge
{
    //todo - EVENT_ADMIN - one would be added per event

    public static String ADMIN = "admin";
    public static String USER = "user";

    private String privledge; // null for USER

    public UserPrivledge() {}

    public UserPrivledge(String privledge)
    {
        this.privledge = USER.equals(privledge) ? null : privledge;
    }

    @DynamoDBIgnore
    public boolean isAdmin()
    {
        return ADMIN.equals(privledge);
    }

    @DynamoDBIgnore
    public String getPrivledgeDisplay()
    {
        return privledge == null ? USER : privledge;
    }

    @DynamoDBAttribute(attributeName = "Privledge")
    public String getPrivledge()
    {
        return privledge;
    }
    public void setPrivledge(String privledge)
    {
        this.privledge = privledge;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof UserPrivledge)) return false;

        UserPrivledge that = (UserPrivledge) o;

        return privledge != null ? privledge.equals(that.privledge) : that.privledge == null;
    }

    @Override
    public int hashCode()
    {
        return privledge != null ? privledge.hashCode() : 0;
    }
}
