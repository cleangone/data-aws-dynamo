package xyz.cleangone.data.manager;

import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import xyz.cleangone.data.aws.dynamo.dao.*;
import xyz.cleangone.data.aws.dynamo.dao.event.EventDao;
import xyz.cleangone.data.aws.dynamo.dao.event.EventDateDao;
import xyz.cleangone.data.aws.dynamo.dao.event.EventParticipantDao;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.image.ImageType;
import xyz.cleangone.data.aws.dynamo.entity.item.CatalogItem;
import xyz.cleangone.data.aws.dynamo.entity.organization.*;
import xyz.cleangone.data.aws.dynamo.entity.person.Person;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.cache.EntityCache;
import xyz.cleangone.data.cache.OrgEntityCache;
import xyz.cleangone.data.manager.event.ItemManager;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class EventManager implements ImageContainerManager
{
    public static final OrgEntityCache<OrgEvent> EVENT_CACHE = new OrgEntityCache<>(EntityType.EVENT);
    public static final OrgEntityCache<EventDate> EVENT_DATE_CACHE = new OrgEntityCache<>(EntityType.EVENTDATE);
    public static final EntityCache<EventParticipant> PARTICIPANT_CACHE = new EntityCache<>(EntityType.PARTICIPANT);

    private final EventDao eventDao;
    private final EventDateDao eventDateDao;
    private final EventParticipantDao eventParticipantDao;
    private final PersonDao personDao;

    private Organization org;
    private OrgEvent event;
    private OrgTag category;
    private CatalogItem item;


    public EventManager()
    {
        eventDao = new EventDao();
        eventDateDao = new EventDateDao();
        eventParticipantDao = new EventParticipantDao();
        personDao = new PersonDao();
    }

    public Organization getOrg() { return org; }
    public void setOrg(Organization org)
    {
        this.org = requireNonNull(org);
        this.event = null;
        this.category = null;
    }

    public List<OrgEvent> getEvents()
    {
        Date start = new Date();
        List<OrgEvent> events = EVENT_CACHE.get(org);
        if (events != null) { return events; }

        events = new ArrayList<>(eventDao.getByOrg(org.getId()));
        events.sort((e1, e2) -> e1.getSortOrder().compareToIgnoreCase(e2.getSortOrder()));
        EVENT_CACHE.put(org, events, start);

        return events;
    }

    public List<OrgEvent> getActiveEvents()
    {
        return getEvents().stream()
            .filter(OrgEvent::getEnabled)
            .filter(OrgEvent::hasTag)
            .collect(Collectors.toList());
    }

    public OrgEvent getActiveEvent(String tag)
    {
        for (OrgEvent event : getActiveEvents())
        {
            if (event.getTag().equals(tag)) { return(event); }
        }

        return null;
    }

    public Map<String, OrgEvent> getEventsById()
    {
        return getEvents().stream()
            .collect(Collectors.toMap(OrgEvent::getId, event -> event));
    }

    public Map<String, OrgEvent> getEventsByName()
    {
        return getEvents().stream()
            .collect(Collectors.toMap(OrgEvent::getName, event -> event));
    }

    public OrgEvent createEvent(String name)
    {
        OrgEvent event = new OrgEvent(org.getId(), name);
        eventDao.save(event);

        return event;
    }

    public OrgEvent getEvent()
    {
        return event;
    }
    public void setEvent(OrgEvent event)
    {
        this.event = requireNonNull(event);
        category = null;
    }

    public void save(OrgEvent event)
    {
        eventDao.save(event);
    }
    public void save()
    {
        eventDao.save(event);
    }

    public List<EventDate> getEventDates()
    {
        Date start = new Date();
        List<EventDate> eventDates = EVENT_DATE_CACHE.get(org);
        if (eventDates != null) { return eventDates; }

        eventDates = eventDateDao.getByOrg(org.getId());
        EVENT_DATE_CACHE.put(org, eventDates, start);

        return eventDates;
    }

    public List<EventDate> getEventDatesByEvent()
    {
        return getEventDates().stream()
            .filter(item -> item.getEventId().equals(event.getId()))
            .collect(Collectors.toList());
    }

    public void save(EventDate eventDate)
    {
        eventDateDao.save(eventDate);
    }
    public void delete(EventDate eventDate)
    {
        eventDateDao.delete(eventDate);
    }

    public void createEventDate(String name)
    {
        EventDate eventDate = new EventDate(org.getId(), event.getId(), name);
        eventDateDao.save(eventDate);
    }

    //
    // Participants
    //

    // participants are cached by eventId
    public List<EventParticipant> getEventParticipants()
    {
        Date start = new Date();
        List<EventParticipant> participants = PARTICIPANT_CACHE.get(event, org.getId());
        if (participants != null) { return participants; }

        participants = eventParticipantDao.getByEvent(event.getId());
        PARTICIPANT_CACHE.put(event, participants, org.getId(), start);

        return participants;
    }

    public void save(EventParticipant participant)
    {
        eventParticipantDao.save(participant);
    }
    public void delete(EventParticipant participant)
    {
        eventParticipantDao.delete(participant);
        PARTICIPANT_CACHE.clear(participant.getEventId());
    }

    public void addEventParticipants(List<Person> people)
    {
        List<String> currParticipantPersonIds = getEventParticipants().stream()
            .map(EventParticipant::getPersonId)
            .collect(Collectors.toList());

        boolean participantAdded = false;
        for (Person person : people)
        {
            if (!currParticipantPersonIds.contains(person.getId()))
            {
                EventParticipant participant = new EventParticipant(person.getId(), event.getId());
                eventParticipantDao.save(participant);
                participantAdded = true;
            }
        }

        if (participantAdded) { PARTICIPANT_CACHE.clear(event.getId()); }
    }

    public EventParticipant getEventParticipant(User user)
    {
        return getEventParticipant(user.getId());
    }

    public EventParticipant getEventParticipant(String personId)
    {
        List<EventParticipant> matchingParticipants = getEventParticipants().stream()
            .filter(p -> p.getPersonId().equals(personId))
            .collect(Collectors.toList());

        if (matchingParticipants.isEmpty()) { return null; }

        // todo - error if > 1

        EventParticipant participant = matchingParticipants.get(0);
        participant.setPerson(personDao.getById(participant.getPersonId()));

        return participant;
    }

    public void addEventParticipant(User user, Set<OrgTag> eventTags)
    {
        if (getEventParticipant(user) != null) { return; }

        EventParticipant participant = new EventParticipant(user.getId(), event.getId());
        participant.setSelfRegistered(true);
        eventParticipantDao.save(participant);
        PARTICIPANT_CACHE.clear(event.getId());

        // todo - these need to be user.roles
//        if (!eventTags.isEmpty())
//        {
//            List<String> eventTagIds = eventTags.stream()
//                .map(OrgTag::getId)
//                .collect(Collectors.toList());
//
//            Person person = personDao.getById(user.getPersonId());
//            person.getTagIds().addAll(eventTagIds);
//            personDao.save(person);
//        }
    }

    // add selectedTags, remove any visibleTags that are not selected
    public void updateEventParticipant(
        EventParticipant participant, List<OrgTag> visibleTags, Set<OrgTag> selectedTags)
    {
        List<String> selectedTagIds = selectedTags.stream()
            .map(OrgTag::getId)
            .collect(Collectors.toList());

        List<String> unselectedTagIds = visibleTags.stream()
            .map(OrgTag::getId)
            .filter(id -> !selectedTagIds.contains(id))
            .collect(Collectors.toList());

        Person person = participant.getPerson();
        person.getTagIds().addAll(selectedTagIds);
        person.getTagIds().removeAll(unselectedTagIds);
        personDao.save(person);
    }

    public void removeEventParticipant(EventParticipant participant, List<OrgTag> eventTags)
    {
        List<String> eventTagIds = eventTags.stream()
            .map(OrgTag::getId)
            .collect(Collectors.toList());

        Person person = participant.getPerson();
        person.getTagIds().removeAll(eventTagIds);
        personDao.save(person);

        eventParticipantDao.delete(participant);
    }

    public void addTagId(String tagId, List<EventParticipant> participants)
    {
        for (EventParticipant participant : participants)
        {
            Person person = participant.getPerson();
            person.addTagId(tagId);
            personDao.save(person);
        }
    }

    public void removeTagId(String tagId, List<EventParticipant> participants)
    {
        for (EventParticipant participant : participants)
        {
            Person person = participant.getPerson();
            person.removeTagId(tagId);
            personDao.save(person);
        }
    }

    //
    // Tags
    //

//    public List<String> getCategoryIds()
//    {
//        return event.getCategoryIds();
//    }
//    public List<String> getEventTagIds(String tagTypeName)
//    {
//        return TagType.CATEGORY_TAG_TYPE.equals(tagTypeName) ?  event.getCategoryIds() : event.getTagIds();
//    }
//
//    public void setEventTagIds(List<String> tagIds, String tagTypeName)
//    {
//        if (TagType.CATEGORY_TAG_TYPE.equals(tagTypeName)) { event.setCategoryIds(tagIds); }
//        else { event.setTagIds(tagIds); }
//    }

//    public List<String> getEventTagIds(String tagTypeName)
//    {
//        return TagType.CATEGORY_TAG_TYPE.equals(tagTypeName) ?  null : event.getTagIds();
//    }
//
//    public void setEventTagIds(List<String> tagIds, String tagTypeName)
//    {
//        if (TagType.CATEGORY_TAG_TYPE.equals(tagTypeName)) { ; }
//        else { event.setTagIds(tagIds); }
//    }

    public OrgTag getCategory()
    {
        return category;
    }
    public void setCategory(OrgTag category)
    {
        this.category = category;
    }

    public List<S3Link> getImages()
    {
        return event.getImages();
    }
    public void addImage(S3Link image)
    {
        event.addImage(image);
    }
    public void deleteImage(S3Link image)
    {
        event.deleteImage(image);
    }
    public List<String> getImageUrls()
    {
        return getImageManager().getUrls();
    }
    public ImageManager getImageManager()
    {
        return new ImageManager(this);
    }
    public S3Link createS3Link(String filePath)
    {
        String seperator = filePath.startsWith("/") ? "" : "/";
        String fullFilePath = "org/" + org.getTag() + "/events/" + event.getTag() + seperator + filePath;
        return eventDao.createS3Link(fullFilePath);
    }

    public String getImageUrl(ImageType imageType)
    {
        if (imageType == ImageType.Banner) { return event.getBannerUrl(); }
        else if (imageType == ImageType.Blurb) { return event.getBlurbBannerUrl(); }
        return null;
    }

    public void setImageUrl(ImageType imageType, String imageUrl)
    {
        if (imageType == ImageType.Banner) { event.setBannerUrl(imageUrl); }
        else if (imageType == ImageType.Blurb) { event.setBlurbBannerUrl(imageUrl); }
    }

    public void setItem(CatalogItem item)
    {
        this.item = item;
    }

    public ItemManager getItemManager(CatalogItem item)
    {
        setItem(item);
        return getItemManager();
    }

    public ItemManager getItemManager()
    {
        return new ItemManager(org, event, item);
    }




    public EventDao getEventDao()
    {
        return eventDao;
    }
}
