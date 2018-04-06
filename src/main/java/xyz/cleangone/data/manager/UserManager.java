package xyz.cleangone.data.manager;

import static java.util.Objects.requireNonNull;

import xyz.cleangone.data.aws.dynamo.dao.UserDao;
import xyz.cleangone.data.aws.dynamo.dao.PersonDao;
import xyz.cleangone.data.aws.dynamo.dao.UserTokenDao;
import xyz.cleangone.data.aws.dynamo.entity.organization.EventParticipant;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgTag;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.person.Person;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.aws.dynamo.entity.person.UserToken;

import java.util.List;
import java.util.stream.Collectors;

public class UserManager
{
    private UserDao userDao = new UserDao();
    private PersonDao personDao = new PersonDao();
    private UserTokenDao userTokenDao = new UserTokenDao();

    private User user;
    private UserToken userToken;

    // super login, w/o org
    public User loginSuper(String email, String password)
    {
        for (User foundUser : userDao.getByEmail(email))
        {
            if (foundUser.getOrgId() == null && foundUser.passwordMatches(password))
            {
                return setUser(foundUser);
            }
        }

        user = null;
        return null;
    }

    // login to specified org, or null for super
    public User login(String email, String password, Organization org)
    {
        requireNonNull(org);
        for (User foundUser : userDao.getByEmail(email))
        {
            // check that org & password match
            if (foundUser.getOrgId() == null || foundUser.getOrgId().equals(org.getId()) &&
                foundUser.passwordMatches(password))
            {
                return setUser(foundUser);
            }
        }

        user = null;
        return null;
    }

    public User loginByToken(String token)
    {
        this.user = null;
        userToken = null;

        if (token == null) { return null; }

        userToken = userTokenDao.getById(token);
        if (userToken == null) { return null; }

        User user = userDao.getById(userToken.getUserId());
        return (user == null ?  null : setUser(user));
    }

    public boolean userIsAdmin(Organization org)
    {
        if (user == null) { return false; }

        return (user.isSuper() || (user.getOrgId().equals(org.getId()) &&  user.isAdmin()));
    }

    public boolean userHasEventAdmin(Organization org, List<OrgTag> eventAdminRoleTags)
    {
        if (user == null || !user.getOrgId().equals(org.getId())) { return false; }

        for (OrgTag tag : eventAdminRoleTags)
        {
            if (user.getTagIds().contains(tag.getId())) { return true; }
        }

        return false;
    }

    public boolean userIsSuper()
    {
        return (user != null && user.isSuper());
    }

    public void logout()
    {
        deleteToken();
        user = null;
    }

    public void deleteToken()
    {
        if (userToken != null)
        {
            userTokenDao.delete(userToken);
            userToken = null;
        }
    }

    public User createUser(String email, String personId, String orgId)
    {
        return createUser(email, null, personId, orgId);
    }

    public User createUser(String email, String password, String personId, String orgId)
    {
        requireNonNull(email);
        requireNonNull(personId);

        if (emailExists(email, orgId))
        {
            // should have been checked
            throw new RuntimeException("Username already exists");
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPersonId(personId);
        newUser.setOrgId(orgId);
        if (password != null) { newUser.setPassword(password); }

        userDao.save(newUser);
        return newUser;
    }

    public boolean emailExists(String email, String orgId)
    {
        List<User> existingUsers = userDao.getByEmail(email).stream()
            .filter(user -> (user.getOrgId() == null || user.getOrgId().equals(orgId)))
            .collect(Collectors.toList());

        return !existingUsers.isEmpty();
    }

    public User getUserWithEmail(String email, String orgId)
    {
        List<User> users = userDao.getByOrg(orgId).stream()
            .filter(user -> email.equals(user.getEmail()))
            .collect(Collectors.toList());

        if (users.isEmpty()) { return null; }

        // todo = log
        if (users.size() > 1) { throw new IllegalStateException("More than one user in org " + orgId + " has email " + email); }

        return users.get(0);
    }

    public boolean passwordMatches(String password) { return user.passwordMatches(password); }
    public void updatePassword(String password)
    {
        user.setPassword(password);
        saveUser();
    }

    public void saveUser()
    {
        if (user != null) { userDao.save(user); }
    }

    public boolean hasUser()
    {
        return (user != null);
    }
    public User getUser()
    {
        return user;
    }
    public void delete(User user)
    {
        userDao.delete(user);
    }
    public void save(User user)
    {
        userDao.save(user);
    }

    public User setUser(User user)
    {
        // todo - verify user.id set and person found
        this.user = user;
        user.setPerson(personDao.getById(user.getPersonId()));

        return user;
    }

    public Person getPerson()
    {
        return user.getPerson();
    }
    public String getPersonFirstName()
    {
        return user == null ? null : user.getPerson().getFirstName();
    }

    public User copyUser()
    {
        if (user == null) { return null; }

        User copiedUser = new User();
        copiedUser.setPersonId(user.getPersonId());
        copiedUser.setEmail(user.getEmail());
        copiedUser.setAddress(user.getAddress());
        copiedUser.setCity(user.getCity());
        copiedUser.setState(user.getState());
        copiedUser.setZip(user.getZip());

        Person copiedPerson = new Person();
        copiedUser.setPerson(copiedPerson);
        copiedPerson.setId(user.getPerson().getId());
        copiedPerson.setFirstName(user.getPerson().getFirstName());
        copiedPerson.setLastName(user.getPerson().getLastName());

        return copiedUser;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }
    public PersonDao getPersonDao()
    {
        return personDao;
    }


    public void verifyEmail(String verifyEmailTokenId)
    {
        if (verifyEmailTokenId == null) { return; }

        UserToken verifyEmailToken = userTokenDao.getById(verifyEmailTokenId);
        if (verifyEmailToken == null) { return; }

        if (user == null)
        {
            // no user login - verify email of emailToken user
            User user = userDao.getById(verifyEmailToken.getUserId());
            user.setEmailVerified(true);
            userDao.save(user);
        }
        else if (user.getId().equals(verifyEmailToken.getUserId()))
        {
            // user logged in by token matches the one in the email token - verify email
            user.setEmailVerified(true);
            userDao.save(user);
        }

        userTokenDao.delete(verifyEmailToken);
    }

    public UserToken cycleToken()
    {
        if (user == null) { throw new IllegalStateException("Cannnot create a token for null user"); }

        // delete current if it exists
        deleteToken();

        // save new token with userId
        userToken = createToken(user);
        return userToken;
    }

    public UserToken createToken()
    {
        return createToken(user);
    }

    public UserToken createToken(User user)
    {
        if (user == null) { return null; }

        // create new token
        UserToken token = new UserToken();
        token.setUserId(user.getId());
        userTokenDao.save(token);

        return token;
    }

    public void addTagId(String tagId, List<User> users)
    {
        for (User user : users)
        {
            user.addTagId(tagId);
            userDao.save(user);
        }
    }

    public void removeTagId(String tagId, List<User> users)
    {
        for (User user : users)
        {
            user.removeTagId(tagId);
            userDao.save(user);
        }
    }

}
