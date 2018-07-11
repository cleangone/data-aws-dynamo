package xyz.cleangone.data.manager;

import xyz.cleangone.data.aws.dynamo.dao.org.TagDao;
import xyz.cleangone.data.aws.dynamo.dao.org.TagTypeDao;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgEvent;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;
import xyz.cleangone.data.aws.dynamo.entity.organization.TagType;
import xyz.cleangone.data.cache.EntityCache;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class TagManager
{
    public static final EntityCache<TagType> TAG_TYPE_CACHE = new EntityCache<>(EntityType.TagType);
    public static final EntityCache<OrgTag> TAG_CACHE = new EntityCache<>(EntityType.Tag);
    private final TagDao tagDao = new TagDao();
    private final TagTypeDao tagTypeDao = new TagTypeDao();

    private Organization org;
    private TagType tagType; // for admin

    public TagManager() { }
    public TagManager(Organization org)
    {
        this.org = org;
    }

    // no external entity should want all the diff tag types mixed together
    private List<OrgTag> getTags()
    {
        Date start = new Date();
        List<OrgTag> tags = TAG_CACHE.get(org);
        if (tags != null) { return tags; }

        List<TagType> tagTypes = new ArrayList<>(tagTypeDao.getByOrg(org.getId()));
        Map<String, TagType> tagTypeById = tagTypes.stream()
            .collect(Collectors.toMap(TagType::getId, tagType -> tagType));

        tags = new ArrayList<>(tagDao.getByOrg(org.getId()));
        tags.forEach(t -> t.setTagType(tagTypeById.get(t.getTagTypeId())));
        tags.sort((tag1, tag2) -> tag1.compareTo(tag2));
        TAG_CACHE.put(org, tags, start);
        return tags;
    }

    public List<TagType> getTagTypes()
    {
        Date start = new Date();
        List<TagType> tagTypes = TAG_TYPE_CACHE.get(org);
        if (tagTypes != null) { return tagTypes; }

        tagTypes = new ArrayList<>(tagTypeDao.getByOrg(org.getId()));
        tagTypes.sort((tagType1, tagType2) -> tagType1.compareTo(tagType2));

        TAG_TYPE_CACHE.put(org, tagTypes, start);
        return tagTypes;
    }

    public TagType getTagType()
    {
        return tagType;
    }
    public void setTagType(TagType tagType)
    {
        this.tagType = tagType;
    }

    public List<OrgTag> getCategories()
    {
        return getTags(TagType.CATEGORY_TAG_TYPE);
    }
    public List<OrgTag> getPersonTags()
    {
        return getTags(TagType.PERSON_TAG_TAG_TYPE);
    }

    public List<OrgTag> getTags(String tagTypeName)
    {
        return getTags().stream()
            .filter(t -> t.isTagType(tagTypeName))
            .collect(Collectors.toList());
    }

    public List<OrgTag> getTags(EntityType entityType)
    {
        return getTags().stream()
            .filter(t -> t.isEntityType(entityType))
            .collect(Collectors.toList());
    }

    public List<OrgTag> getTags(String tagTypeName, Map<String, OrgEvent> eventsById)
    {
        List<OrgTag>  tags = getTags(tagTypeName);
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

    public List<OrgTag> getOrgTags(String tagTypeName)
    {
        return getTags(tagTypeName).stream()
            .filter(t -> t.getEventId() == null)
            .collect(Collectors.toList());
    }

    public List<OrgTag> getEventVisibleTags(String tagTypeName, OrgEvent event)
    {
        if (event == null) { return new ArrayList<OrgTag>(); }

        return getTags(tagTypeName).stream()
            .filter(t -> t.getEventId() == null || t.getEventId().equals(event.getId()))
            .collect(Collectors.toList());
    }

    public List<OrgTag> getEventTags(String tagTypeName, String eventId)
    {
        return getTags(tagTypeName).stream()
            .filter(t -> eventId.equals(t.getEventId()))
            .collect(Collectors.toList());
    }

    public List<OrgTag> getEventTags(String tagTypeName, String eventId, List<String> tagIds)
    {
        return getTags(tagTypeName).stream()
            .filter(t -> eventId.equals(t.getEventId()) || tagIds.contains(t.getId()))
            .collect(Collectors.toList());
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

    public void createTag(String name, TagType tagType, OrgEvent event)
    {
        OrgTag tag = new OrgTag(name, tagType, event.getId());
        tagDao.save(tag);
    }

    public void createTag(String name)
    {
        createTag(name, tagType);
    }
    public void createTag(String name, TagType tagType)
    {
        OrgTag tag = new OrgTag(name, tagType);
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

    public void createTagType(String name, EntityType entityType)
    {
        if (!getTags(name).isEmpty()) { return; }

        TagType tagType = new TagType(name, entityType, org.getId());
        tagTypeDao.save(tagType);
        TAG_TYPE_CACHE.clear(org);
    }

    public void save(TagType tagType)
    {
        tagTypeDao.save(tagType);
    }
    public void delete(TagType tagType)
    {
        tagTypeDao.delete(tagType);
        TAG_TYPE_CACHE.clear(org);
    }

    public void setOrg(Organization org)
    {
        this.org = requireNonNull(org);
    }
}
