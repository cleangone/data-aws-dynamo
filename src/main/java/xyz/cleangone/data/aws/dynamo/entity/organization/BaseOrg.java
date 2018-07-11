package xyz.cleangone.data.aws.dynamo.entity.organization;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseNamedEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityField;
import xyz.cleangone.data.aws.dynamo.entity.image.ImageContainer;

import java.util.ArrayList;
import java.util.List;

@DynamoDBTable(tableName="BaseOrgDummy")
public class BaseOrg extends BaseNamedEntity implements ImageContainer
{
    public static final EntityField TAG_FIELD = new EntityField("baseOrg.tag", "Tag Name");
    public static final EntityField BANNER_URL_FIELD = new EntityField("baseOrg.bannerUrl", "Banner URL");
    public static final EntityField BANNER_BKGND_COLOR_FIELD = new EntityField("baseOrg.bannerBackgroundColor", "Background Color");
    public static final EntityField BANNER_TEXT_COLOR_FIELD = new EntityField("baseOrg.bannerTextColor", "Text Color");

    public static final EntityField BANNER_TEXT_HTML_FIELD = new EntityField("baseOrg.bannerTextHtml", "Text HTML");
    public static final EntityField BANNER_TEXT_SIZE_FIELD = new EntityField("baseOrg.bannerTextSize", "Text Size");
    public static final EntityField BANNER_TEXT_LINE_HEIGHT_FIELD = new EntityField("baseOrg.bannerTextLineHeight", "Line Height");
    public static final EntityField BANNER_TEXT_BOTTOM_OFFSET_FIELD = new EntityField("baseOrg.bannerTextBotOffset", "Bottom Offset");

    public static final EntityField BANNER_TEXT_MOBILE_HTML_FIELD = new EntityField("baseOrg.bannerTextMobileHtml", "Mobile Text HTML");
    public static final EntityField BANNER_TEXT_MOBILE_SIZE_FIELD = new EntityField("baseOrg.bannerTextMobileSize", "Text Size");
    public static final EntityField BANNER_TEXT_MOBILE_LINE_HEIGHT_FIELD = new EntityField("baseOrg.bannerTextMobileLineHeight", "Line Height");
    public static final EntityField BANNER_TEXT_MOBILE_BOTTOM_OFFSET_FIELD = new EntityField("baseOrg.bannerTextMobileBotOffset", "Bottom Offset");

    public static final EntityField BANNER_MOBILE_OFFSET_X_FIELD = new EntityField("baseOrg.bannerMobileOffsetX", "Mobile Offset X");
    public static final EntityField BANNER_MOBILE_OFFSET_Y_FIELD = new EntityField("baseOrg.bannerMobileOffsetY", "Mobile Offset Y");
    public static final EntityField BAR_BKGND_COLOR_FIELD = new EntityField("baseOrg.barBackgroundColor", "Bar Background Color");
    public static final EntityField NAV_BKGND_COLOR_FIELD = new EntityField("baseOrg.navBackgroundColor", "Background Color");
    public static final EntityField NAV_TEXT_COLOR_FIELD = new EntityField("baseOrg.NavTextColor", "Text Color");
    public static final EntityField NAV_SELECTED_TEXT_COLOR_FIELD = new EntityField("baseOrg.NavSelectedTextColor", "Selected Text Color");
    public static final EntityField INTRO_HTML_FIELD = new EntityField("baseOrg.introHtml", "Intro HTML");
    public static final EntityField BLURB_HTML_FIELD = new EntityField("orgEvent.blurbHtml", "Blurb HTML");

    private String tag;
    private List<S3Link> images;
    private String bannerUrl;
    private String bannerBackgroundColor;
    private String bannerTextColor;
    private BannerText bannerText = new BannerText();
    private BannerText bannerTextMobile = new BannerText();
    private String bannerMobileOffsetX;  // reposition the banner image so it can be centered for mobile devices
    private String bannerMobileOffsetY;
    private String barBackgroundColor;
    private String navBackgroundColor;
    private String navTextColor;
    private String navSelectedTextColor;
    private String introHtml;
    private String blurbBannerUrl;
    private String blurbHtml;

    public BaseOrg()
    {
        super();
    }
    public BaseOrg(String name)
    {
        super(name);
    }

    public String get(EntityField field)
    {
        if (TAG_FIELD.equals(field)) return getTag();
        else if (BANNER_URL_FIELD.equals(field)) return getBannerUrl();
        else if (BANNER_BKGND_COLOR_FIELD.equals(field)) return getBannerBackgroundColor();
        else if (BANNER_TEXT_COLOR_FIELD.equals(field)) return getBannerTextColor();
        else if (BANNER_TEXT_HTML_FIELD.equals(field)) return bannerText.getHtml();
        else if (BANNER_TEXT_SIZE_FIELD.equals(field)) return bannerText.getSize();
        else if (BANNER_TEXT_LINE_HEIGHT_FIELD.equals(field)) return bannerText.getLineHeight();
        else if (BANNER_TEXT_BOTTOM_OFFSET_FIELD.equals(field)) return bannerText.getBottomOffset();
        else if (BANNER_TEXT_MOBILE_HTML_FIELD.equals(field)) return bannerTextMobile.getHtml();
        else if (BANNER_TEXT_MOBILE_SIZE_FIELD.equals(field)) return bannerTextMobile.getSize();
        else if (BANNER_TEXT_MOBILE_LINE_HEIGHT_FIELD.equals(field)) return bannerTextMobile.getLineHeight();
        else if (BANNER_TEXT_MOBILE_BOTTOM_OFFSET_FIELD.equals(field)) return bannerTextMobile.getBottomOffset();
        else if (BANNER_MOBILE_OFFSET_X_FIELD.equals(field)) return getBannerMobileOffsetX();
        else if (BANNER_MOBILE_OFFSET_Y_FIELD.equals(field)) return getBannerMobileOffsetY();
        else if (BAR_BKGND_COLOR_FIELD.equals(field)) return getBarBackgroundColor();
        else if (NAV_BKGND_COLOR_FIELD.equals(field)) return getNavBackgroundColor();
        else if (NAV_TEXT_COLOR_FIELD.equals(field)) return getNavTextColor();
        else if (NAV_SELECTED_TEXT_COLOR_FIELD.equals(field)) return getNavSelectedTextColor();
        else if (INTRO_HTML_FIELD.equals(field)) return getIntroHtml();
        else if (BLURB_HTML_FIELD.equals(field)) return getBlurbHtml();
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (TAG_FIELD.equals(field)) setTag(value);
        else if (BANNER_URL_FIELD.equals(field)) setBannerUrl(value);
        else if (BANNER_BKGND_COLOR_FIELD.equals(field)) setBannerBackgroundColor(value);
        else if (BANNER_TEXT_COLOR_FIELD.equals(field)) setBannerTextColor(value);
        else if (BANNER_TEXT_HTML_FIELD.equals(field)) bannerText.setHtml(value);
        else if (BANNER_TEXT_SIZE_FIELD.equals(field)) bannerText.setSize(value);
        else if (BANNER_TEXT_LINE_HEIGHT_FIELD.equals(field)) bannerText.setLineHeight(value);
        else if (BANNER_TEXT_BOTTOM_OFFSET_FIELD.equals(field)) bannerText.setBottomOffset(value);
        else if (BANNER_TEXT_MOBILE_HTML_FIELD.equals(field)) bannerTextMobile.setHtml(value);
        else if (BANNER_TEXT_MOBILE_SIZE_FIELD.equals(field)) bannerTextMobile.setSize(value);
        else if (BANNER_TEXT_MOBILE_LINE_HEIGHT_FIELD.equals(field)) bannerTextMobile.setLineHeight(value);
        else if (BANNER_TEXT_MOBILE_BOTTOM_OFFSET_FIELD.equals(field)) bannerTextMobile.setBottomOffset(value);
        else if (BANNER_MOBILE_OFFSET_X_FIELD.equals(field)) setBannerMobileOffsetX(value);
        else if (BANNER_MOBILE_OFFSET_Y_FIELD.equals(field)) setBannerMobileOffsetY(value);
        else if (NAV_BKGND_COLOR_FIELD.equals(field)) setNavBackgroundColor(value);
        else if (BAR_BKGND_COLOR_FIELD.equals(field)) setBarBackgroundColor(value);
        else if (NAV_TEXT_COLOR_FIELD.equals(field)) setNavTextColor(value);
        else if (NAV_SELECTED_TEXT_COLOR_FIELD.equals(field)) setNavSelectedTextColor(value);
        else if (INTRO_HTML_FIELD.equals(field)) setIntroHtml(value);
        else if (BLURB_HTML_FIELD.equals(field)) setBlurbHtml(value);
        else super.set(field, value);
    }

    @DynamoDBIgnore
    public boolean hasTag()
    {
        return tag != null;
    }

    @DynamoDBAttribute(attributeName="Tag")
    public String getTag()
    {
        return tag;
    }
    public void setTag(String tag)
    {
        this.tag = tag;
    }

    @DynamoDBAttribute(attributeName="Images")
    public List<S3Link> getImages()
    {
        return images;
    }
    public void setImages(List<S3Link> images)
    {
        this.images = images;
    }
    public void addImage(S3Link image)
    {
        if (images == null) { images = new ArrayList<>(); }
        images.add(image);
    }
    public void deleteImage(S3Link image)
    {
        images.remove(image);
    }

    @DynamoDBAttribute(attributeName="BannerUrl")
    public String getBannerUrl()
    {
        return bannerUrl;
    }
    public void setBannerUrl(String bannerUrl)
    {
        this.bannerUrl = bannerUrl;
    }

//    @DynamoDBAttribute(attributeName="BannerHtml")
//    public String getBannerHtml()
//    {
//        return bannerHtml;
//    }
//    public void setBannerHtml(String bannerHtml)
//    {
//        this.bannerHtml = bannerHtml;
//    }

    @DynamoDBAttribute(attributeName="BannerBackgroundColor")
    public String getBannerBackgroundColor()
    {
        return bannerBackgroundColor;
    }
    public void setBannerBackgroundColor(String bannerBackgroundColor) {
        this.bannerBackgroundColor = bannerBackgroundColor;
    }

    @DynamoDBAttribute(attributeName="BannerTextColor")
    public String getBannerTextColor()
    {
        return bannerTextColor;
    }
    public void setBannerTextColor(String bannerTextColor)
    {
        this.bannerTextColor = bannerTextColor;
    }

    @DynamoDBAttribute(attributeName="BannerTextStyle")
    public BannerText getBannerText()
    {
        return bannerText;
    }
    public void setBannerText(BannerText bannerText)
    {
        this.bannerText = bannerText;
    }

    @DynamoDBAttribute(attributeName="BannerTextMobile")
    public BannerText getBannerTextMobile()
    {
        return bannerTextMobile;
    }
    public void setBannerTextMobile(BannerText bannerTextMobile)
    {
        this.bannerTextMobile = bannerTextMobile;
    }



//
//    @DynamoDBAttribute(attributeName="BannerTextSize")
//    public String getBannerTextSize()
//    {
//        return bannerTextSize;
//    }
//    public void setBannerTextSize(String bannerTextSize)
//    {
//        this.bannerTextSize = bannerTextSize;
//    }
//
//    @DynamoDBAttribute(attributeName="BannerTextDropshadowColor")
//    public String getBannerTextDropshadowColor()
//    {
//        return bannerTextDropshadowColor;
//    }
//    public void setBannerTextDropshadowColor(String bannerTextDropshadowColor)
//    {
//        this.bannerTextDropshadowColor = bannerTextDropshadowColor;
//    }

    @DynamoDBAttribute(attributeName="BannerMobileOffsetX")
    public String getBannerMobileOffsetX()
    {
        return bannerMobileOffsetX;
    }
    public void setBannerMobileOffsetX(String bannerMobileOffsetX)
    {
        this.bannerMobileOffsetX = bannerMobileOffsetX;
    }

    @DynamoDBAttribute(attributeName="BannerMobileOffsetY")
    public String getBannerMobileOffsetY()
    {
        return bannerMobileOffsetY;
    }
    public void setBannerMobileOffsetY(String bannerMobileOffsetY)
    {
        this.bannerMobileOffsetY = bannerMobileOffsetY;
    }

    @DynamoDBAttribute(attributeName="BarBackgroundColor")
    public String getBarBackgroundColor()
    {
        return barBackgroundColor;
    }
    public void setBarBackgroundColor(String barBackgroundColor)
    {
        this.barBackgroundColor = barBackgroundColor;
    }

    @DynamoDBAttribute(attributeName="NavBackgroundColor")
    public String getNavBackgroundColor()
    {
        return navBackgroundColor;
    }
    public void setNavBackgroundColor(String navBackgroundColor)
    {
        this.navBackgroundColor = navBackgroundColor;
    }

    @DynamoDBAttribute(attributeName="NavTextColor")
    public String getNavTextColor()
    {
        return navTextColor;
    }
    public void setNavTextColor(String navTextColor)
    {
        this.navTextColor = navTextColor;
    }

    @DynamoDBAttribute(attributeName="NavSelectedTextColor")
    public String getNavSelectedTextColor()
    {
        return navSelectedTextColor;
    }
    public void setNavSelectedTextColor(String navSelectedTextColor)
    {
        this.navSelectedTextColor = navSelectedTextColor;
    }

    @DynamoDBAttribute(attributeName="IntroHtml")
    public String getIntroHtml()
    {
        return introHtml;
    }
    public void setIntroHtml(String introHtml)
    {
        this.introHtml = introHtml;
    }

    @DynamoDBAttribute(attributeName = "BlurbBannerUrl")
    public String getBlurbBannerUrl()
    {
        return blurbBannerUrl;
    }
    public void setBlurbBannerUrl(String blurbBannerUrl)
    {
        this.blurbBannerUrl = blurbBannerUrl;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BaseOrg)) return false;
        if (!super.equals(o)) return false;

        BaseOrg baseOrg = (BaseOrg) o;

        if (getTag() != null ? !getTag().equals(baseOrg.getTag()) : baseOrg.getTag() != null) return false;
        if (getImages() != null ? !getImages().equals(baseOrg.getImages()) : baseOrg.getImages() != null) return false;
        if (getBannerUrl() != null ? !getBannerUrl().equals(baseOrg.getBannerUrl()) : baseOrg.getBannerUrl() != null) return false;
        if (getBannerBackgroundColor() != null ? !getBannerBackgroundColor().equals(baseOrg.getBannerBackgroundColor()) : baseOrg.getBannerBackgroundColor() != null) return false;
        if (getBannerTextColor() != null ? !getBannerTextColor().equals(baseOrg.getBannerTextColor()) : baseOrg.getBannerTextColor() != null) return false;
        if (getBannerText() != null ? !getBannerText().equals(baseOrg.getBannerText()) : baseOrg.getBannerText() != null) return false;
        if (getBannerTextMobile() != null ? !getBannerTextMobile().equals(baseOrg.getBannerTextMobile()) : baseOrg.getBannerTextMobile() != null) return false;
        if (getBannerMobileOffsetX() != null ? !getBannerMobileOffsetX().equals(baseOrg.getBannerMobileOffsetX()) : baseOrg.getBannerMobileOffsetX() != null) return false;
        if (getBannerMobileOffsetY() != null ? !getBannerMobileOffsetY().equals(baseOrg.getBannerMobileOffsetY()) : baseOrg.getBannerMobileOffsetY() != null) return false;
        if (getBarBackgroundColor() != null ? !getBarBackgroundColor().equals(baseOrg.getBarBackgroundColor()) : baseOrg.getBarBackgroundColor() != null) return false;
        if (getNavBackgroundColor() != null ? !getNavBackgroundColor().equals(baseOrg.getNavBackgroundColor()) : baseOrg.getNavBackgroundColor() != null) return false;
        if (getNavTextColor() != null ? !getNavTextColor().equals(baseOrg.getNavTextColor()) : baseOrg.getNavTextColor() != null) return false;
        if (getNavSelectedTextColor() != null ? !getNavSelectedTextColor().equals(baseOrg.getNavSelectedTextColor()) : baseOrg.getNavSelectedTextColor() != null) return false;
        if (getIntroHtml() != null ? !getIntroHtml().equals(baseOrg.getIntroHtml()) : baseOrg.getIntroHtml() != null) return false;
        if (getBlurbBannerUrl() != null ? !getBlurbBannerUrl().equals(baseOrg.getBlurbBannerUrl()) : baseOrg.getBlurbBannerUrl() != null) return false;
        return getBlurbHtml() != null ? getBlurbHtml().equals(baseOrg.getBlurbHtml()) : baseOrg.getBlurbHtml() == null;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (getTag() != null ? getTag().hashCode() : 0);
        result = 31 * result + (getImages() != null ? getImages().hashCode() : 0);
        result = 31 * result + (getBannerUrl() != null ? getBannerUrl().hashCode() : 0);
        result = 31 * result + (getBannerBackgroundColor() != null ? getBannerBackgroundColor().hashCode() : 0);
        result = 31 * result + (getBannerTextColor() != null ? getBannerTextColor().hashCode() : 0);
        result = 31 * result + (getBannerText() != null ? getBannerText().hashCode() : 0);
        result = 31 * result + (getBannerTextMobile() != null ? getBannerTextMobile().hashCode() : 0);
        result = 31 * result + (getBannerMobileOffsetX() != null ? getBannerMobileOffsetX().hashCode() : 0);
        result = 31 * result + (getBannerMobileOffsetY() != null ? getBannerMobileOffsetY().hashCode() : 0);
        result = 31 * result + (getBarBackgroundColor() != null ? getBarBackgroundColor().hashCode() : 0);
        result = 31 * result + (getNavBackgroundColor() != null ? getNavBackgroundColor().hashCode() : 0);
        result = 31 * result + (getNavTextColor() != null ? getNavTextColor().hashCode() : 0);
        result = 31 * result + (getNavSelectedTextColor() != null ? getNavSelectedTextColor().hashCode() : 0);
        result = 31 * result + (getIntroHtml() != null ? getIntroHtml().hashCode() : 0);
        result = 31 * result + (getBlurbBannerUrl() != null ? getBlurbBannerUrl().hashCode() : 0);
        result = 31 * result + (getBlurbHtml() != null ? getBlurbHtml().hashCode() : 0);
        return result;
    }
}


