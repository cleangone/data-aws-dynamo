package xyz.cleangone.data.manager;

import static java.util.Objects.*;

import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import xyz.cleangone.data.aws.dynamo.dao.org.OrgDao;
import xyz.cleangone.data.aws.dynamo.dao.PersonDao;
import xyz.cleangone.data.aws.dynamo.dao.user.UserDao;
import xyz.cleangone.data.aws.dynamo.dao.org.PaymentProcessorDao;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityLastTouched;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.image.ImageType;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.organization.PaymentProcessor;
import xyz.cleangone.data.aws.dynamo.entity.person.Person;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.cache.EntityCache;
import xyz.cleangone.data.manager.event.BidManager;
import xyz.cleangone.data.manager.event.ItemManager;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class OrgManager implements ImageContainerManager
{
    public static final EntityCache<Person> PERSON_CACHE = new EntityCache<>(EntityType.Person);

    private OrgDao orgDao;
    private PersonDao personDao;
    private UserDao userDao;
    private PaymentProcessorDao paymentProcessorDao = new PaymentProcessorDao();

    protected Organization org;
    protected PaymentProcessor paymentProcessor;
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
        Date start = new Date();
        List<Person> people = PERSON_CACHE.get(org);
        if (people != null) { return people; }

        people = personDao.getByOrg(getOrgId());
        PERSON_CACHE.put(org, people, start);

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

    public Organization createOrg(String name)
    {
        Organization newOrg = new Organization(name);
        save(newOrg);

        return newOrg;
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
        paymentProcessor = null;
    }

    public Organization setOrgById(String orgId)
    {
        org = orgDao.getById(requireNonNull(orgId));
//        event = null;
        paymentProcessor = null;

        return org;
    }

    public EntityLastTouched getOrgLastTouched()
    {
        return (org == null ? null : orgDao.getEntityLastTouched(org.getId()));
    }

    public Organization findOrg(String tag)
    {
        return orgDao.getByTag(tag);
    }
    public String getOrgId()
    {
        return requireNonNull(org).getId();
    }




    public PaymentProcessor getPaymentProcessor()
    {
        if (paymentProcessor == null)
        {
            paymentProcessor = (org == null || org.getPaymentProcessorId() == null) ? null : paymentProcessorDao.getById(org.getPaymentProcessorId());
        }

        return paymentProcessor;
    }

    public PaymentProcessor createPaymentProcessor()
    {
        paymentProcessor = new PaymentProcessor();
        paymentProcessorDao.save(paymentProcessor);

        org.setPaymentProcessorId(paymentProcessor.getId());
        orgDao.save(org);

        return paymentProcessor;
    }


    //
    // Images
    //
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

    public String getImageUrl(ImageType imageType)
    {
        if (imageType == ImageType.Banner) { return getOrg().getBannerUrl(); }
        else if (imageType == ImageType.Blurb) { return getOrg().getBlurbBannerUrl(); }
        return null;
    }

    public void setImageUrl(ImageType imageType, String imageUrl)
    {
        if (imageType == ImageType.Banner) { getOrg().setBannerUrl(imageUrl); }
        else if (imageType == ImageType.Blurb) { getOrg().setBlurbBannerUrl(imageUrl); }
    }

    public ItemManager getItemManager()
    {
        return new ItemManager(org);
    }
    public ActionManager getActionManager()
    {
        return new ActionManager(org);
    }
    public BidManager getBidManager()
    {
        return new BidManager(org);
    }

    public TagManager getTagManager()
    {
        tagManager.setOrg(org);
        return tagManager;
    }

    public NotificationManager getNotificationManager()
    {
        return new NotificationManager(org.getId());
    }
    public ImageManager getImageManager()
    {
        return new ImageManager(this);
    }

    public OrgDao getOrgDao()
    {
        return orgDao;
    }
    public PaymentProcessorDao getPaymentProcessorDao()
    {
        return paymentProcessorDao;
    }
}
