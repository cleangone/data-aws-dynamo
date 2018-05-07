package xyz.cleangone.data.manager;

import static java.util.Objects.requireNonNull;

import xyz.cleangone.data.aws.dynamo.dao.AddressDao;
import xyz.cleangone.data.aws.dynamo.dao.user.UserDao;
import xyz.cleangone.data.aws.dynamo.dao.PersonDao;
import xyz.cleangone.data.aws.dynamo.dao.user.UserTokenDao;
import xyz.cleangone.data.aws.dynamo.entity.person.Address;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.aws.dynamo.entity.person.UserToken;

import java.util.List;

public class UserManager
{
    private UserDao userDao = new UserDao();
    private PersonDao personDao = new PersonDao();
    private AddressDao addressDao = new AddressDao();
    private UserTokenDao userTokenDao = new UserTokenDao();

    private User user;
    private UserToken userToken;

    public User login(String email, String password)
    {
        User foundUser = getUserWithEmail(email);
        if (foundUser != null && foundUser.passwordMatches(password)) { return setUser(foundUser); }

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

    public boolean userIsSuperAdmin()
    {
        return (user != null && user.isSuperAdmin());
    }

    public boolean userIsOrgAdmin(String orgId)
    {
        if (user == null) { return false; }
        return (user.isSuperAdmin() || user.isOrgAdmin(orgId));
    }

    public boolean userIsEventAdmin(String orgId)
    {
        if (user == null) { return false; }
        return (user.isEventAdmin(orgId));
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

    public User createUser(String email, String firstName, String lastName, String orgId)
    {
        if (emailExists(requireNonNull(email)))
        {
            // should have been checked
            throw new RuntimeException("Email already exists");
        }

        User newUser = new User();
        newUser.setEmail(email);
        if (firstName != null) { newUser.setFirstName(firstName); }
        if (lastName != null) { newUser.setLastName(lastName); }
        if (orgId != null) { newUser.addOrgId(orgId); }

        userDao.save(newUser);
        return newUser;
    }

    public boolean emailExists(String email)
    {
        return getUserWithEmail(email) != null;
    }

    public User getUserWithEmail(String email)
    {
        List<User> users = userDao.getByEmail(email);

        if (users.isEmpty()) { return null; }

        // todo - log
        if (users.size() > 1) { throw new IllegalStateException("More than one user has email " + email); }

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
        return user;
    }

    public String getFirstName()
    {
        return user == null ? null : user.getFirstName();
    }

    public User copyUser()
    {
        if (user == null) { return null; }

        User copiedUser = new User();
        copiedUser.setFirstName(user.getFirstName());
        copiedUser.setLastName(user.getLastName());
        copiedUser.setEmail(user.getEmail());
        copiedUser.setAddressId(user.getAddressId());

        return copiedUser;
    }


    //
    // Address
    //
    public Address getAddress()
    {
        return (user == null || user.getAddressId() == null ? null : addressDao.getById(user.getAddressId()));
    }

    public Address createAddress()
    {
        Address address = new Address();
        addressDao.save(address);

        user.setAddressId(address.getId());
        userDao.save(user);

        return address;
    }

    public Address copyAddress()
    {
        Address copiedAddress = new Address();

        Address address = getAddress();
        if (address != null)
        {
            copiedAddress.setStreetAddress(address.getStreetAddress());
            copiedAddress.setCity(address.getCity());
            copiedAddress.setState(address.getState());
            copiedAddress.setZip(address.getZip());
        }

        return copiedAddress;
    }

    //
    // Email
    //
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

    public UserDao getUserDao()
    {
        return userDao;
    }
    public PersonDao getPersonDao()
    {
        return personDao;
    }
    public AddressDao getAddressDao()
    {
        return addressDao;
    }
}
