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
    public static final EntityField PHONE_FIELD = new EntityField("user.phone", "Phone");
    public static final EntityField ACCEPT_TEXTS_FIELD = new EntityField("user.acceptText", "Accept Texts");
    public static final EntityField SHOW_BID_CONFIRM_FIELD = new EntityField("user.showBidConfirm", "Require Bid Confirmation");
    public static final EntityField SHOW_QUICK_BID_FIELD = new EntityField("user.showQuickBid", "Show QuickBid Button");
    public static final EntityField LAST_FIRST_FIELD = new EntityField("user.lastfirst", "Last, First");
    public static final EntityField ORG_ADMIN_FIELD = new EntityField("user.transient.orgadmin", "Admin Privledge");
    public static final EntityField TAGS_FIELD = new EntityField("user.tags", "Roles");

    private List<String> watchedItemIds;  // todo - would have to be moved out of core user
    private boolean showBidConfirm;
    private boolean showQuickBid;

    private String orgId;  // null for super
    private String personId;
    private String encryptedPassword;
    private String email;
    private boolean emailVerified;
    private String addressId;

    private String phone;  // todo - multiple phones, with type and acceptTexts
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

    @DynamoDBIgnore public boolean hasPrivledge(String orgId)
    {
        return (this.orgId == null || this.orgId.equals(orgId));
    }
    @DynamoDBIgnore public boolean isWatching(String itemId)
    {
        return getWatchedItemIds().contains(itemId);
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
        else if (PHONE_FIELD.equals(field)) return getPhone();
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (EMAIL_FIELD.equals(field)) setEmail(value);
        else if (PHONE_FIELD.equals(field)) setPhone(value);
        else super.set(field, value);
    }

    public boolean getBoolean(EntityField field)
    {
        if (ACCEPT_TEXTS_FIELD.equals(field)) return getAcceptTexts();
        else if (SHOW_BID_CONFIRM_FIELD.equals(field)) return getShowBidConfirm();
        else if (SHOW_QUICK_BID_FIELD.equals(field)) return getShowQuickBid();
        else return super.getBoolean(field);
    }

    public void setBoolean(EntityField field, boolean value)
    {
        if (ACCEPT_TEXTS_FIELD.equals(field)) setAcceptTexts(value);
        else if (SHOW_BID_CONFIRM_FIELD.equals(field)) setShowBidConfirm(value);
        else if (SHOW_QUICK_BID_FIELD.equals(field)) setShowQuickBid(value);
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

    @DynamoDBAttribute(attributeName="AddressId")
    public String getAddressId()
    {
        return addressId;
    }
    public void setAddressId(String addressId)
    {
        this.addressId = addressId;
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

    @DynamoDBAttribute(attributeName="ShowBidConfirm")
    public boolean getShowBidConfirm()
    {
        return showBidConfirm;
    }
    public void setShowBidConfirm(boolean showBidConfirm)
    {
        this.showBidConfirm = showBidConfirm;
    }

    @DynamoDBAttribute(attributeName="ShowQuickBid")
    public boolean getShowQuickBid()
    {
        return showQuickBid;
    }
    public void setShowQuickBid(boolean showQuickBid)
    {
        this.showQuickBid = showQuickBid;
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

    @DynamoDBAttribute(attributeName="WatchedItemIds")
    public List<String> getWatchedItemIds()
    {
        if (watchedItemIds == null) { watchedItemIds = new ArrayList<>(); }
        return watchedItemIds;
    }
    public void setWatchedItemIds(List<String> watchedItemIds)
    {
        this.watchedItemIds = watchedItemIds;
    }
    public void addWatchedItemId(String itemId)
    {
        if (!getWatchedItemIds().contains(itemId)) { watchedItemIds.add(itemId); }
    }
    public void removeWatchedItemId(String itemId)
    {
        watchedItemIds.remove(itemId);
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


