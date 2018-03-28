package xyz.cleangone.data.aws.dynamo.entity.person;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseMixinEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;
import xyz.cleangone.util.Crypto;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

// NOTE: user no longer uses super.name as the username
@DynamoDBTable(tableName="User")
public class User extends BaseMixinEntity
{
    public static final EntityField PASSWORD_FIELD = new EntityField("user.password", "Password");
    public static final EntityField EMAIL_FIELD = new EntityField("user.email", "Email");
    public static final EntityField ADDRESS_FIELD = new EntityField("user.address", "Address");
    public static final EntityField CITY_FIELD = new EntityField("user.city", "City");
    public static final EntityField STATE_FIELD = new EntityField("user.state", "State");
    public static final EntityField ZIP_FIELD = new EntityField("user.zip", "Zip");
    public static final EntityField PHONE_FIELD = new EntityField("user.phone", "Phone");
    public static final EntityField ACCEPT_TEXTS_FIELD = new EntityField("user.acceptText", "Accept Texts");
    public static final EntityField LAST_FIRST_FIELD = new EntityField("user.lastfirst", "Last, First");
    public static final EntityField ORG_ADMIN_FIELD = new EntityField("user.transient.orgadmin", "Admin Privledge");
    public static final EntityField TAGS_FIELD = new EntityField("user.tags", "Roles");

    private String orgId;  // null for super
    private String personId;
    private String encryptedPassword;
    private String email;
    private boolean emailVerified;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private boolean acceptTexts;
    private List<UserPrivledge> userPrivledges;
    private List<String> tagIds;
    private String tagsCsv; // transient
    private Person person; // transient


    @DynamoDBIgnore
    public boolean isSuper()
    {
        return orgId == null;
    }

    @DynamoDBIgnore
    public boolean isAdmin()
    {
        if (userPrivledges == null) { return false; }

        for (UserPrivledge userPrivledge : userPrivledges)
        {
            if (userPrivledge.isAdmin()) { return true; }
        }

        return false;
    }

    @DynamoDBIgnore
    public boolean hasPrivledge(String orgId)
    {
        return (this.orgId == null || this.orgId.equals(orgId));
    }

    @DynamoDBIgnore
    public String getPassword()
    {
        return null;
    }
    public void setPassword(String password)
    {
        setEncryptedPassword(encrypt(password));
    }
    public boolean passwordMatches(String password)
    {
        return (encryptedPassword != null && encryptedPassword.equals(encrypt(password)));
    }

    public String get(EntityField field)
    {
        if (EMAIL_FIELD.equals(field)) return getEmail();
        else if (ADDRESS_FIELD.equals(field)) return getAddress();
        else if (CITY_FIELD.equals(field)) return getCity();
        else if (STATE_FIELD.equals(field)) return getState();
        else if (ZIP_FIELD.equals(field)) return getZip();
        else if (PHONE_FIELD.equals(field)) return getPhone();
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (EMAIL_FIELD.equals(field)) setEmail(value);
        else if (ADDRESS_FIELD.equals(field)) setAddress(value);
        else if (CITY_FIELD.equals(field)) setCity(value);
        else if (STATE_FIELD.equals(field)) setState(value);
        else if (ZIP_FIELD.equals(field)) setZip(value);
        else if (PHONE_FIELD.equals(field)) setPhone(value);
        else super.set(field, value);
    }

    public boolean getBoolean(EntityField field)
    {
        if (ACCEPT_TEXTS_FIELD.equals(field)) return getAcceptTexts();
        else return super.getBoolean(field);
    }

    public void setBoolean(EntityField field, boolean value)
    {
        if (ACCEPT_TEXTS_FIELD.equals(field)) setAcceptTexts(value);
        else super.setBoolean(field, value);
    }

    @DynamoDBAttribute(attributeName="OrgId")
    public String getOrgId()
    {
        return orgId;
    }
    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    @DynamoDBAttribute(attributeName="PersonId")
    public String getPersonId()
    {
        return personId;
    }
    public void setPersonId(String personId)
    {
        this.personId = personId;
    }

    @DynamoDBAttribute(attributeName="EncryptedPassword")
    public String getEncryptedPassword()
    {
        return encryptedPassword;
    }
    public void setEncryptedPassword(String encryptedPassword)
    {
        this.encryptedPassword = encryptedPassword;
    }

    @DynamoDBAttribute(attributeName="Email")
    public String getEmail() { return email;}
    public void setEmail(String email) {this.email = email;}

    @DynamoDBAttribute(attributeName="EmailVerified")
    public boolean getEmailVerified()
    {
        return emailVerified;
    }
    public void setEmailVerified(boolean emailVerified)
    {
        this.emailVerified = emailVerified;
    }

    @DynamoDBAttribute(attributeName="Address")
    public String getAddress()
    {
        return address;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    @DynamoDBAttribute(attributeName="City")
    public String getCity()
    {
        return city;
    }
    public void setCity(String city)
    {
        this.city = city;
    }

    @DynamoDBAttribute(attributeName="State")
    public String getState()
    {
        return state;
    }
    public void setState(String state)
    {
        this.state = state;
    }

    @DynamoDBAttribute(attributeName="Zip")
    public String getZip()
    {
        return zip;
    }
    public void setZip(String zip)
    {
        this.zip = zip;
    }

    @DynamoDBAttribute(attributeName="Phone")
    public String getPhone()
    {
        return phone;
    }
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    @DynamoDBAttribute(attributeName="AcceptTexts")
    public boolean getAcceptTexts()
    {
        return acceptTexts;
    }
    public void setAcceptTexts(boolean acceptTexts)
    {
        this.acceptTexts = acceptTexts;
    }

    @DynamoDBAttribute(attributeName = "UserPrivledges")
    public List<UserPrivledge> getUserPrivledges()
    {
        return userPrivledges;
    }
    public void setUserPrivledges(List<UserPrivledge> userPrivledges)
    {
        this.userPrivledges = userPrivledges;
    }
    public void addUserPrivledge(String privledge)
    {
        if (userPrivledges == null) { userPrivledges = new ArrayList<>(); }

        UserPrivledge userPrivledge = new UserPrivledge(privledge);
        if (!userPrivledges.contains(userPrivledge))
        {
            userPrivledges.add(userPrivledge);
        }
    }

    @DynamoDBAttribute(attributeName="TagIds")
    public List<String> getTagIds()
    {
        if (tagIds == null) { tagIds = new ArrayList<>(); }
        return tagIds;
    }
    public void setTagIds(List<String> tagIds)
    {
        this.tagIds = tagIds;
    }
    public void addTagId(String tagId)
    {
        if (!getTagIds().contains(tagId)) { tagIds.add(tagId); }
    }
    public void removeTagId(String tagId)
    {
        tagIds.remove(tagId);
    }


    @DynamoDBIgnore
    public String getTagsCsv() { return tagsCsv; }
    public void setTagsCsv(Map<String, OrgTag> tagsById)
    {
        tagsCsv = getCsv(tagsById);
    }


    // todo - copied from Person
    private String getCsv(Map<String, OrgTag> tagsById)
    {
        if (getTagIds().isEmpty()) { return ""; }

        List<String> tagNames = tagIds.stream()
            .filter(id -> tagsById.containsKey(id)) // map may be of a subset of tags
            .map(id -> tagsById.get(id))
            .map(OrgTag::getName)
            .collect(Collectors.toList());

        Collections.sort(tagNames);
        return tagNames.stream().collect(Collectors.joining(", "));
    }

    private String encrypt(String s)
    {
        return Crypto.encrypt(Objects.requireNonNull(s));
    }

    @DynamoDBIgnore
    public Person getPerson()
    {
        return person;
    }
    public void setPerson(Person person)
    {
        this.person = person;
    }

    @DynamoDBIgnore
    public String getLastCommaFirst()
    {
        return person.getLastCommaFirst();
    }

    @DynamoDBIgnore
    public boolean isOrgAdmin()
    {
        return isAdmin();
    }
    public void setOrgAdmin(boolean orgAdmin)
    {
        if (userPrivledges != null)
        {
            for (UserPrivledge userPrivledge : userPrivledges)
            {
                // todo - junky - refactor after Event Admin privledge added
                userPrivledge.setPrivledge((orgAdmin ? UserPrivledge.ADMIN : null));
                return;

            }
        }

        addUserPrivledge(orgAdmin ? UserPrivledge.ADMIN : UserPrivledge.USER);
    }
}


