package xyz.cleangone.data.aws.dynamo.entity.person;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgEvent;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;
import xyz.cleangone.util.Crypto;

import java.util.*;
import java.util.stream.Collectors;


@DynamoDBTable(tableName="User")
public class User extends BasePerson
{
    public static final EntityField PASSWORD_FIELD = new EntityField("user.password", "Password");
    public static final EntityField EMAIL_FIELD = new EntityField("user.email", "Email");
    public static final EntityField PHONE_FIELD = new EntityField("user.phone", "Phone");
    public static final EntityField ACCEPT_TEXTS_FIELD = new EntityField("user.acceptText", "Accept Texts");
    public static final EntityField SHOW_BID_CONFIRM_FIELD = new EntityField("user.showBidConfirm", "Require Bid Confirmation");
    public static final EntityField SHOW_QUICK_BID_FIELD = new EntityField("user.showQuickBid", "Show QuickBid Button");
    public static final EntityField LAST_FIRST_FIELD = new EntityField("user.lastfirst", "Last, First");
    public static final EntityField ADMIN_FIELD = new EntityField("user.transient.admin", "Admin");

    private List<String> watchedItemIds;  // todo - doesn't belong in core user
    private boolean showBidConfirm;
    private boolean showQuickBid;

    private List<String> orgIds;  // orgs the user has interacted with
    private String encryptedPassword;
    private String email;
    private boolean emailVerified;
    private String addressId;

    private String phone;  // todo - multiple phones, with type and acceptTexts
    private boolean acceptTexts;
    private List<AdminPrivledge> adminPrivledges;

    @DynamoDBIgnore
    public boolean isSuperAdmin()
    {
        if (adminPrivledges == null) { return false; }
        for (AdminPrivledge adminPrivledge : adminPrivledges)
        {
            if (adminPrivledge.isSuperAdmin()) { return true; }
        }

        return false;
    }

    @DynamoDBIgnore
    public boolean isOrgAdmin(String orgId)
    {
        if (adminPrivledges == null) { return false; }
        for (AdminPrivledge adminPrivledge : adminPrivledges)
        {
            if (adminPrivledge.isOrgAdmin(orgId)) { return true; }
        }

        return false;
    }

    @DynamoDBIgnore
    public boolean setOrgAdmin(String orgId)
    {
        if (adminPrivledges == null) { return false; }
        for (AdminPrivledge adminPrivledge : adminPrivledges)
        {
            if (adminPrivledge.isOrgAdmin(orgId)) { return true; }
        }

        return false;
    }


    @DynamoDBIgnore
    public boolean isEventAdmin(String orgId)
    {
        if (adminPrivledges == null) { return false; }
        for (AdminPrivledge adminPrivledge : adminPrivledges)
        {
            if (adminPrivledge.isEventAdmin(orgId)) { return true; }
        }

        return false;
    }

    @DynamoDBIgnore
    public boolean isEventAdmin(String orgId, String eventId)
    {
        if (adminPrivledges == null) { return false; }
        for (AdminPrivledge adminPrivledge : adminPrivledges)
        {
            if (adminPrivledge.isEventAdmin(orgId, eventId)) { return true; }
        }

        return false;
    }

    @DynamoDBIgnore
    public List<String> getAdminPrivledgeEventIds(String orgId)
    {
        if (adminPrivledges == null) { return Collections.emptyList(); }

        return adminPrivledges.stream()
            .filter(priv -> priv.isEventAdmin(orgId))
            .map(AdminPrivledge::getEventId)
            .collect(Collectors.toList());
    }

    @DynamoDBIgnore public boolean hasPassword()
    {
        return encryptedPassword != null;
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
        setEncryptedPassword(password == null || password.length() == 0 ? null : encrypt(password));
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

    @DynamoDBAttribute(attributeName="OrgIds")
    public List<String> getOrgIds()
    {
        return orgIds;
    }
    public void setOrgIds(List<String> orgIds)
    {
        this.orgIds = orgIds;
    }

    // return true if orgid added, false otherwise
    public boolean addOrgId(String orgId)
    {
        if (orgIds == null) { orgIds = new ArrayList<>(); }
        if (orgIds.contains(orgId)) { return false; }

        orgIds.add(orgId);
        return true;
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

    @DynamoDBAttribute(attributeName = "AdminPrivledges")
    public List<AdminPrivledge> getAdminPrivledges()
    {
        return adminPrivledges;
    }
    public void setAdminPrivledges(List<AdminPrivledge> adminPrivledges)
    {
        this.adminPrivledges = adminPrivledges;
    }
    public void addAdminPrivledge(OrgEvent event)
    {
        addAdminPrivledge(new AdminPrivledge(event));
    }
    public void addAdminPrivledge(AdminPrivledge adminPrivledge)
    {
        if (adminPrivledges == null) { adminPrivledges = new ArrayList<>(); }
        if (!adminPrivledges.contains(adminPrivledge))
        {
            adminPrivledges.add(adminPrivledge);
        }
    }
    public void removeAdminPrivledge(OrgEvent event)
    {
        removeAdminPrivledge(new AdminPrivledge(event));
    }
    public void removeAdminPrivledge(AdminPrivledge adminPrivledge)
    {
        if (adminPrivledges == null) { return; }
        adminPrivledges.remove(adminPrivledge);
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

    private String encrypt(String s)
    {
        return Crypto.encrypt(Objects.requireNonNull(s));
    }
}


