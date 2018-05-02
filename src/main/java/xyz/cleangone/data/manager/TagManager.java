package xyz.cleangone.data.manager;

import xyz.cleangone.data.aws.dynamo.dao.org.TagDao;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgEvent;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;
import xyz.cleangone.data.cache.EntityCache;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class TagManager
{
    public static final EntityCache<OrgTag> TAG_CACHE = new EntityCache<>(EntityType.Tag);
    private final TagDao tagDao;

    private Organization org;

    public TagManager()
    {
        tagDao = new TagDao();
    }

    public TagManager(Organization org)
    {
        this.org = org;
        tagDao = new TagDao();
    }

    // no external entity should want all the diff tag types mixed together
    private List<OrgTag> getTags()
    {
        Date start = new Date();
        List<OrgTag> tags = TAG_CACHE.get(org);
        if (tags != null) { return tags; }

        tags = new ArrayList<>(tagDao.getByOrg(org.getId()));
        tags.sort((tag1, tag2) -> tag1.compareTo(tag2));
        TAG_CACHE.put(org, tags, start);
        return tags;
    }

    public static String getSingularName(OrgTag.TagType tagType)
    {
        if (tagType == OrgTag.TagType.PersonTag) { return "Tag"; }
        else if (tagType == OrgTag.TagType.Category) { return "Category"; }
        else if (tagType == OrgTag.TagType.UserRole) { return "User Role"; }
        else return "Unknown";
    }

    public static String getPluralName(OrgTag.TagType tagType)
    {
        if (tagType == OrgTag.TagType.PersonTag) { return "Tags"; }
        else if (tagType == OrgTag.TagType.Category) { return "Categories"; }
        else if (tagType == OrgTag.TagType.UserRole) { return "User Roles"; }
        else return "Unknown";
    }

    public List<OrgTag> getPersonTags()
    {
        return getTags(OrgTag.TagType.PersonTag);
    }
    public List<OrgTag> getCategories()
    {
        return getTags(OrgTag.TagType.Category);
    }

    public List<OrgTag> getTags(OrgTag.TagType tagType)
    {
        return getTags().stream()
            .filter(t -> t.isTagType(tagType))
            .collect(Collectors.toList());
    }

    public List<OrgTag> getTags(OrgTag.TagType tagType, Map<String, OrgEvent> eventsById)
    {
        List<OrgTag>  tags = getTags(tagType);
        tags.forEach(tag ->  {
            if (eventsById.containsKey(tag.getEventId())) { tag.setEventName(eventsById.get(tag.getEventId()).getName()); } });

        return tags;
    }

    public List<OrgTag> getTags(List<String> tagIds)
    {
        if (tagIds == null) { return new ArrayList<OrgTag>(); }

        return getTags().stream()
            .filter(t -> tagIds.contains(t.getId()))
            .collect(Collectors.toList());
    }

    public List<OrgTag> getOrgTags(OrgTag.TagType tagType)
    {
        return getTags(tagType).stream()
            .filter(t -> t.getEventId() == null)
            .collect(Collectors.toList());
    }

    public List<OrgTag> getEventVisibleTags(OrgTag.TagType tagType, OrgEvent event)
    {
        if (event == null) { return new ArrayList<OrgTag>(); }

        List<String> tagIds = event.getTagIds(tagType);
        return getTags(tagType).stream()
            .filter(t ->
                event.getId().equals(t.getEventId()) ||
                (t.getEventId() == null && tagIds != null && tagIds.contains(t.getId())))
            .collect(Collectors.toList());
    }

    public List<OrgTag> getEventTags(OrgTag.TagType tagType, String eventId)
    {
        return getTags(tagType).stream()
            .filter(t -> eventId.equals(t.getEventId()))
            .collect(Collectors.toList());
    }

    public List<OrgTag> getEventTags(OrgTag.TagType tagType, String eventId, List<String> tagIds)
    {
        return getTags(tagType).stream()
            .filter(t -> eventId.equals(t.getEventId()) || tagIds.contains(t.getId()))
            .collect(Collectors.toList());
    }

    public List<OrgTag> getEventAdminRoleTags()
    {
        return getTags(OrgTag.TagType.UserRole).stream()
            .filter(t -> t.getEventId() != null)
            .filter(t -> OrgTag.ADMIN_ROLE_NAME.equals(t.getName()))
            .collect(Collectors.toList());
    }

    public Map<String, OrgTag> getEventAdminRoleTagsById()
    {
        return getEventAdminRoleTags().stream()
            .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
    }

    public Map<String, OrgTag> getTagsById()
    {
        return getTagsById(getTags());
    }
    public Map<String, OrgTag> getTagsById(List<OrgTag> tags)
    {
        return tags.stream()
            .collect(Collectors.toMap(OrgTag::getId, Function.identity()));
    }

    public void createTag(String name, OrgTag.TagType tagType)
    {
        OrgTag tag = new OrgTag(name, tagType, org.getId());
        tagDao.save(tag);
    }

    public void createTag(String name, OrgTag.TagType tagType, OrgEvent event)
    {
        OrgTag tag = new OrgTag(name, tagType, org.getId(), event.getId());
        tagDao.save(tag);
    }

    public void save(OrgTag tag)
    {
        tagDao.save(tag);
    }
    public void delete(OrgTag tag)
    {
        tagDao.delete(tag);
    }

    public void setOrg(Organization org)
    {
        this.org = requireNonNull(org);
    }
}
