package xyz.cleangone.data.manager;

import static java.util.Objects.*;

import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import xyz.cleangone.data.aws.dynamo.dao.OrgDao;
import xyz.cleangone.data.aws.dynamo.dao.PersonDao;
import xyz.cleangone.data.aws.dynamo.dao.UserDao;
import xyz.cleangone.data.aws.dynamo.entity.base.OrgLastTouched;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.person.Person;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.cache.EntityCache;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class OrgManager implements ImageContainerManager
{
    private static final EntityCache<Person> PERSON_CACHE = new EntityCache<>();

    private OrgDao orgDao;
    private PersonDao personDao;
    private UserDao userDao;

    protected Organization org;
    private TagManager tagManager;

    public OrgManager()
    {
        personDao = new PersonDao();
        orgDao = new OrgDao();
        userDao = new UserDao();

        tagManager = new TagManager();
    }

    public List<Organization> getAll()
    {
        return orgDao.getAll();
    }
    
    public List<Person> getPeople()
    {
        List<Person> people = PERSON_CACHE.get(org.getId());
        if (people != null) { return people; }

        people = personDao.getByOrg(getOrgId());
        PERSON_CACHE.set(people, org.getId());

        return people;
    }

    public Map<String, Person> getPeopleByIdMap()
    {
       return getPeople().stream()
            .collect(Collectors.toMap(Person::getId, person -> person));
    }

    public List<Person> getPeopleByTag(String tagId)
    {
        return personDao.getByTag(tagId);
    }

    public void addTagId(String tagId, List<Person> people)
    {
        for (Person person : people)
        {
            person.addTagId(tagId);
            personDao.save(person);
        }
    }

    public void removeTagId(String tagId, List<Person> people)
    {
        for (Person person : people)
        {
            person.removeTagId(tagId);
            personDao.save(person);
        }
    }

    public void setPerson(Person person)
    {
        personDao.save(person);
    }

    public void createPerson(String firstName, String lastName)
    {
        Person person = new Person(getOrgId(), firstName, lastName);
        personDao.save(person);
    }

    public void deletePerson(Person person)
    {
        personDao.delete(person);
    }

    public List<User> getUsers()
    {
        return userDao.getByOrg(getOrgId());
    }

    public void save()
    {
        if (org != null) { orgDao.save(org); }
    }
    public void save(Organization org)
    {
       orgDao.save(org);
    }
    public Organization getOrg()
    {
        return org;
    }

    public void setOrg(Organization org)
    {
        this.org = org;
    }

    public Organization setOrgById(String orgId)
    {
        org = orgDao.getById(requireNonNull(orgId));
//        event = null;
        return org;
    }

    public OrgLastTouched getOrgLastTouched()
    {
        return (org == null ? null : orgDao.getLastTouch(org.getId()));
    }

    public Organization findOrg(String tag)
    {
        return orgDao.getByTag(tag);
    }
    public String getOrgId()
    {
        return requireNonNull(org).getId();
    }

    public List<S3Link> getImages()
    {
        return getOrg().getImages();
    }
    public void addImage(S3Link image)
    {
        org.addImage(image);
    }
    public void deleteImage(S3Link image)
    {
        org.deleteImage(image);
    }
    public List<String> getImageUrls()
    {
        return getImageManager().getUrls();
    }
    public S3Link createS3Link(String filePath)
    {
        String seperator = filePath.startsWith("/") ? "" : "/";
        String fullFilePath = "org/" + org.getTag() + "/intro/" + seperator + filePath;
        return orgDao.createS3Link(fullFilePath);
    }

    public String getPrimaryUrl()
    {
        return getOrg().getBannerUrl();
    }
    public void setPrimaryUrl(String bannerUrl)
    {
        getOrg().setBannerUrl(bannerUrl);
    }

    public ActionManager getActionManager()
    {
        return new ActionManager(org);
    }

    public TagManager getTagManager()
    {
        tagManager.setOrg(org);
        return tagManager;
    }

    public ImageManager getImageManager()
    {
        return new ImageManager(this);
    }

    public OrgDao getOrgDao()
    {
        return orgDao;
    }
}
