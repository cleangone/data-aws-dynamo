package xyz.cleangone.data.manager;

import xyz.cleangone.data.aws.dynamo.dao.ActionDao;
import xyz.cleangone.data.aws.dynamo.entity.action.Action;
import xyz.cleangone.data.aws.dynamo.entity.base.EntityType;
import xyz.cleangone.data.aws.dynamo.entity.item.CartItem;
import xyz.cleangone.data.aws.dynamo.entity.item.PurchaseItem;
import xyz.cleangone.data.aws.dynamo.entity.organization.EventParticipant;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgEvent;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.person.Person;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.aws.dynamo.entity.action.ActionType;
import xyz.cleangone.data.aws.dynamo.entity.purchase.Cart;
import xyz.cleangone.data.cache.EntityCache;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class ActionManager
{
    public static final EntityCache<Action> ACTION_CACHE_BY_EVENT = new EntityCache<>(EntityType.Action);
    public static final EntityCache<Action> ACTION_CACHE_BY_PERSON = new EntityCache<>(EntityType.Action, 50);

    private final ActionDao actionDao = new ActionDao();
    private final Organization org;

    public ActionManager(Organization org)
    {
        this.org = requireNonNull(org);
    }

    public List<Action> getActionsByTargetEvent(OrgEvent targetEvent)
    {
        Date start = new Date();
        List<Action> actions = ACTION_CACHE_BY_EVENT.get(targetEvent, org.getId());
        if (actions == null)
        {
            actions = actionDao.getByTargetEventId(targetEvent.getId());
            ACTION_CACHE_BY_EVENT.put(targetEvent, actions, org.getId(), start);
        }

        return actions;
    }

    public List<Action> getActionsBySourcePerson(String sourcePersonId)
    {
//        Date start = new Date();
//        List<Action> actions = ACTION_CACHE_BY_PERSON.get(sourcePerson, org.getId());
//        if (actions == null)
//        {
            return actionDao.getBySourcePersonId(sourcePersonId);
//            ACTION_CACHE_BY_PERSON.put(sourcePersonId, actions, org.getId(), start);
//        }

//        return actions;
    }

    public List<Action> getActionsBySourcePerson(String sourcePersonId, List<ActionType> actionTypes)
    {
        return getActionsBySourcePerson(sourcePersonId).stream()
            .filter(a -> actionTypes.contains(a.getActionType()))
            .collect(Collectors.toList());
    }

    public List<Action> getActionsBySourcePerson(String sourcePersonId, String eventId)
    {
        return getActionsBySourcePerson(sourcePersonId).stream()
            .filter(a -> eventId.equals(a.getTargetEventId()))
            .collect(Collectors.toList());
    }

    public void createActions(User user, Cart cart)
    {
        for (CartItem cartItem : cart.getItems())
        {
            Action action = createAction(user, cartItem);
            save(action);
        }
    }

    public Action createAction(User user, CartItem item)
    {
        if (item.isDonation()) { return createDonation(user, item); }
        else if (item.isPledgeFulfillment()) { return createPledgeFulfillment(user, item); }
        else return createPurchase(user, item);
    }

    // todo - lots of problems here...
    public Action createBid(User user, PurchaseItem item, OrgEvent event)
    {
        return createAction(user, event, ActionType.Bid, item.getPrice())
            .withDescription(item.getName());
    }

    private Action createDonation(User user, CartItem item)
    {
        return createAction(user, item, ActionType.Donated)
            .withTargetPerson(item.getTargetParticipant());
    }

    private Action createPledgeFulfillment(User user, CartItem item)
    {
        return createAction(user, item, ActionType.FulfilledPledge)
            .withDescription(item.getPledgeFulfillmentActionDesc())
            .withTargetPerson(requireNonNull(item.getTargetParticipant()));
    }

    private Action createPurchase(User user, CartItem item)
    {
        return createAction(user, item, ActionType.Purchased)
            .withDescription(item.getCatalogItem().getName());
    }

    public Action createPledge(
        User user, BigDecimal amount, String description, OrgEvent targetEvent, EventParticipant targetParticipant)
    {
        return createAction(user, targetEvent, ActionType.Pledged)
            .withIterationAmount(amount)
            .withDescription(description)
            .withTargetPerson(requireNonNull(targetParticipant));
    }

    public Action createAction(User user, CartItem item, ActionType actionType)
    {
        return createAction(user, item.getEvent(), actionType, item.getPrice());
    }

    public Action createAction(User user, OrgEvent event, ActionType actionType, BigDecimal amount)
    {
        return new Action(org.getId(), actionType, event, user, amount);
    }

    public Action createAction(User user, OrgEvent event, ActionType actionType)
    {
        return new Action(org.getId(), actionType, event, user);
    }

    public static BigDecimal sumAmount(List<Action> actions)
    {
        return actions.stream()
            .filter(Action::hasAmount)
            .map(Action::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void saveAction(User user, OrgEvent event, ActionType actionType)
    {
        save(createAction(user, event, actionType));
    }
    public void save(Action action)
    {
        actionDao.save(action);
    }
}
