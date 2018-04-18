package xyz.cleangone.data.aws.dynamo.entity.organization;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.base.ImageContainer;

import java.util.List;

import static java.util.Objects.requireNonNull;

@DynamoDBTable(tableName="Event")
public class OrgEvent extends BaseOrg implements ImageContainer
{
    public static final EntityField DISPLAY_COL_FIELD = new EntityField("orgEvent.displayCol", "Main Page Display Col");
    public static final EntityField DISPLAY_ORDER_FIELD = new EntityField("orgEvent.displayOrder", "Display Order");
    public static final EntityField USE_ORG_BANNER_FIELD = new EntityField("orgEvent.useOrgBanner", "Use Organization Banner");
    public static final EntityField BLURB_HTML_FIELD = new EntityField("orgEvent.blurbHtml", "Blurb HTML");
    public static final EntityField DISPLAY_CATEGORIES_FIELD = new EntityField("orgEvent.displayCategories", "Display Categories");
    public static final EntityField ACCEPT_DONATIONS_FIELD = new EntityField("orgEvent.acceptDonations", "Accept Donations");
    public static final EntityField ACCEPT_PLEDGES_FIELD = new EntityField("orgEvent.acceptPledges", "Accept Per-Iteration Pledges");
    public static final EntityField EVENT_COMPLETED_FIELD = new EntityField("orgEvent.eventCompleted", "Event Completed");
    public static final EntityField ITER_LABEL_SINGULAR_FIELD = new EntityField("orgEvent.iterationLabelSingular", "Iteration Label Singular");
    public static final EntityField ITER_COUNT_LABEL_PLURAL_FIELD = new EntityField("orgEvent.iterationLabelPlural", "Iteration Label Plural");
    public static final EntityField ESTIMATED_ITERATIONS_FIELD = new EntityField("orgEvent.estimatedIterations", "Estimated Iterations");
    public static final EntityField USER_CAN_REGISTER_FIELD = new EntityField("orgEvent.userCanRegister", "User Can Register Themself");

    public enum ColType { LeftCol, CenterCol, RightCol }

    private String orgId;
    private ColType displayCol;
    private String displayOrder;
    private boolean useOrgBanner;

    // todo - change to personTagIds
    private String blurbHtml;
    private List<String> tagIds; // the org-wide tags this event is interested in
    private List<String> categoryIds; // the org-wide categories this event is interested in
    private boolean displayCategories;
    private boolean acceptDonations;
    private boolean acceptPledges;
    private String iterationLabelSingular;
    private String iterationLabelPlural;
    private int estimatedIterations;
    private boolean userCanRegister;
    private boolean eventCompleted;

    public OrgEvent()
    {
        super();
    }

    public OrgEvent(String orgId, String name)
    {
        super(requireNonNull(name));
        setOrgId(requireNonNull(orgId));
    }

    @DynamoDBIgnore
    public String getSortOrder() { return displayOrder == null ? "zzz" : displayOrder; }  // null will sort to end of list

    @DynamoDBIgnore
    public String getIterationLabel(int iterations) { return iterations == 1 ? iterationLabelSingular : iterationLabelPlural; }

    public String get(EntityField field)
    {
        if (DISPLAY_COL_FIELD.equals(field)) return getDisplayColString();
        else if (BLURB_HTML_FIELD.equals(field)) return getBlurbHtml();
        else if (ITER_LABEL_SINGULAR_FIELD.equals(field)) return getIterationLabelSingular();
        else if (ITER_COUNT_LABEL_PLURAL_FIELD.equals(field)) return getIterationLabelPlural();
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (DISPLAY_COL_FIELD.equals(field)) setDisplayColString(value);
        else if (BLURB_HTML_FIELD.equals(field)) setBlurbHtml(value);
        else if (ITER_LABEL_SINGULAR_FIELD.equals(field)) setIterationLabelSingular(value);
        else if (ITER_COUNT_LABEL_PLURAL_FIELD.equals(field)) setIterationLabelPlural(value);
        else super.set(field, value);
    }

    public boolean getBoolean(EntityField field)
    {
        if (DISPLAY_CATEGORIES_FIELD.equals(field)) return getDisplayCategories();
        else if (USE_ORG_BANNER_FIELD.equals(field)) return getUseOrgBanner();
        else if (ACCEPT_DONATIONS_FIELD.equals(field)) return getAcceptDonations();
        else if (ACCEPT_PLEDGES_FIELD.equals(field)) return getAcceptPledges();
        else if (EVENT_COMPLETED_FIELD.equals(field)) return getEventCompleted();
        else if (USER_CAN_REGISTER_FIELD.equals(field)) return getUserCanRegister();
        else return super.getBoolean(field);
    }

    public void setBoolean(EntityField field, boolean value)
    {
        if (DISPLAY_CATEGORIES_FIELD.equals(field)) setDisplayCategories(value);
        else if (USE_ORG_BANNER_FIELD.equals(field)) setUseOrgBanner(value);
        else if (ACCEPT_DONATIONS_FIELD.equals(field)) setAcceptDonations(value);
        else if (ACCEPT_PLEDGES_FIELD.equals(field)) setAcceptPledges(value);
        else if (EVENT_COMPLETED_FIELD.equals(field)) setEventCompleted(value);
        else if (USER_CAN_REGISTER_FIELD.equals(field)) setUserCanRegister(value);
        else super.setBoolean(field, value);
    }

    public int getInt(EntityField field)
    {
        if (ESTIMATED_ITERATIONS_FIELD.equals(field)) return getEstimatedIterations();
        else return super.getInt(field);
    }

    public void setInt(EntityField field, int value)
    {
        if (ESTIMATED_ITERATIONS_FIELD.equals(field)) setEstimatedIterations(value);
        else super.setInt(field, value);
    }

    @DynamoDBIgnore
    public String getDisplayColString()
    {
        if (displayCol == ColType.LeftCol) { return "Left"; }
        else if (displayCol == ColType.CenterCol) { return "Center"; }
        else if (displayCol == ColType.RightCol) { return "Right"; }
        else return "";
    }

    @DynamoDBIgnore
    public void setDisplayColString(String displayCol)
    {
        if (displayCol == null) { setDisplayCol(null); }
        else if (displayCol.trim().toLowerCase().startsWith("l")) { setDisplayCol(ColType.LeftCol); }
        else if (displayCol.trim().toLowerCase().startsWith("c")) { setDisplayCol(ColType.CenterCol); }
        else if (displayCol.trim().toLowerCase().startsWith("r")) { setDisplayCol(ColType.RightCol); }
        else setDisplayCol(null);;
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

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    public ColType getDisplayCol()
    {
        return displayCol;
    }
    public void setDisplayCol(ColType displayCol)
    {
        this.displayCol = displayCol;
    }

    @DynamoDBAttribute(attributeName = "DisplayOrder")
    public String getDisplayOrder()
    {
        return displayOrder;
    }
    public void setDisplayOrder(String displayOrder)
    {
        this.displayOrder = displayOrder;
    }

    @DynamoDBAttribute(attributeName = "UseOrgBanner")
    public boolean getUseOrgBanner()
    {
        return useOrgBanner;
    }
    public void setUseOrgBanner(boolean useOrgBanner)
    {
        this.useOrgBanner = useOrgBanner;
    }

    @DynamoDBAttribute(attributeName = "BlurbHtml")
    public String getBlurbHtml()
    {
        return blurbHtml;
    }
    public void setBlurbHtml(String blurbHtml)
    {
        this.blurbHtml = blurbHtml;
    }

    @DynamoDBAttribute(attributeName = "EventCompleted")
    public boolean getEventCompleted()
    {
        return eventCompleted;
    }
    public void setEventCompleted(boolean eventCompleted)
    {
        this.eventCompleted = eventCompleted;
    }

    @DynamoDBAttribute(attributeName = "TagIds")
    public List<String> getTagIds()
    {
        return tagIds;
    }
    public void setTagIds(List<String> tagIds)
    {
        this.tagIds = tagIds;
    }

    @DynamoDBAttribute(attributeName = "CategoryIds")
    public List<String> getCategoryIds()
    {
        return categoryIds;
    }
    public void setCategoryIds(List<String> categoryIds)
    {
        this.categoryIds = categoryIds;
    }

    @DynamoDBAttribute(attributeName = "DisplayCategories")
    public boolean getDisplayCategories()
    {
        return displayCategories;
    }
    public void setDisplayCategories(boolean displayCategories)
    {
        this.displayCategories = displayCategories;
    }

    @DynamoDBAttribute(attributeName = "AcceptDonations")
    public boolean getAcceptDonations()
    {
        return acceptDonations;
    }
    public void setAcceptDonations(boolean acceptDonations)
    {
        this.acceptDonations = acceptDonations;
    }

    @DynamoDBAttribute(attributeName = "AcceptPledges")
    public boolean getAcceptPledges()
    {
        return acceptPledges;
    }
    public void setAcceptPledges(boolean acceptPledges)
    {
        this.acceptPledges = acceptPledges;
    }

    @DynamoDBAttribute(attributeName = "IterationLabelSingular")
    public String getIterationLabelSingular()
    {
        return iterationLabelSingular;
    }
    public void setIterationLabelSingular(String iterationLabelSingular) { this.iterationLabelSingular = iterationLabelSingular; }

    @DynamoDBAttribute(attributeName = "IterationLabelPlural")
    public String getIterationLabelPlural()
    {
        return iterationLabelPlural;
    }
    public void setIterationLabelPlural(String iterationLabelPlural) { this.iterationLabelPlural = iterationLabelPlural; }

    @DynamoDBAttribute(attributeName = "EstimatedIterations")
    public int getEstimatedIterations()
    {
        return estimatedIterations;
    }
    public void setEstimatedIterations(int estimatedIterations)
    {
        this.estimatedIterations = estimatedIterations;
    }

    @DynamoDBAttribute(attributeName = "UserCanRegister")
    public boolean getUserCanRegister()
    {
        return userCanRegister;
    }
    public void setUserCanRegister(boolean userCanRegister)
    {
        this.userCanRegister = userCanRegister;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof OrgEvent)) return false;
        if (!super.equals(o)) return false;

        OrgEvent orgEvent = (OrgEvent) o;

        if (getUseOrgBanner() != orgEvent.getUseOrgBanner()) return false;
        if (getDisplayCategories() != orgEvent.getDisplayCategories()) return false;
        if (getAcceptDonations() != orgEvent.getAcceptDonations()) return false;
        if (getAcceptPledges() != orgEvent.getAcceptPledges()) return false;
        if (getEstimatedIterations() != orgEvent.getEstimatedIterations()) return false;
        if (getUserCanRegister() != orgEvent.getUserCanRegister()) return false;
        if (getEventCompleted() != orgEvent.getEventCompleted()) return false;
        if (getOrgId() != null ? !getOrgId().equals(orgEvent.getOrgId()) : orgEvent.getOrgId() != null) return false;
        if (getDisplayCol() != orgEvent.getDisplayCol()) return false;
        if (getDisplayOrder() != null ? !getDisplayOrder().equals(orgEvent.getDisplayOrder()) : orgEvent.getDisplayOrder() != null) return false;
        if (getBlurbHtml() != null ? !getBlurbHtml().equals(orgEvent.getBlurbHtml()) : orgEvent.getBlurbHtml() != null) return false;
        if (getTagIds() != null ? !getTagIds().equals(orgEvent.getTagIds()) : orgEvent.getTagIds() != null) return false;
        if (getCategoryIds() != null ? !getCategoryIds().equals(orgEvent.getCategoryIds()) : orgEvent.getCategoryIds() != null) return false;
        if (getIterationLabelSingular() != null ? !getIterationLabelSingular().equals(orgEvent.getIterationLabelSingular()) : orgEvent.getIterationLabelSingular() != null)
            return false;
        return getIterationLabelPlural() != null ? getIterationLabelPlural().equals(orgEvent.getIterationLabelPlural()) : orgEvent.getIterationLabelPlural() == null;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (getOrgId() != null ? getOrgId().hashCode() : 0);
        result = 31 * result + (getDisplayCol() != null ? getDisplayCol().hashCode() : 0);
        result = 31 * result + (getDisplayOrder() != null ? getDisplayOrder().hashCode() : 0);
        result = 31 * result + (getUseOrgBanner() ? 1 : 0);
        result = 31 * result + (getBlurbHtml() != null ? getBlurbHtml().hashCode() : 0);
        result = 31 * result + (getTagIds() != null ? getTagIds().hashCode() : 0);
        result = 31 * result + (getCategoryIds() != null ? getCategoryIds().hashCode() : 0);
        result = 31 * result + (getDisplayCategories() ? 1 : 0);
        result = 31 * result + (getAcceptDonations() ? 1 : 0);
        result = 31 * result + (getAcceptPledges() ? 1 : 0);
        result = 31 * result + (getIterationLabelSingular() != null ? getIterationLabelSingular().hashCode() : 0);
        result = 31 * result + (getIterationLabelPlural() != null ? getIterationLabelPlural().hashCode() : 0);
        result = 31 * result + getEstimatedIterations();
        result = 31 * result + (getUserCanRegister() ? 1 : 0);
        result = 31 * result + (getEventCompleted() ? 1 : 0);
        return result;
    }
}


