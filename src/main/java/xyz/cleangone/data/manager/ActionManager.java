package xyz.cleangone.data.manager;

import xyz.cleangone.data.aws.dynamo.dao.ActionDao;
import xyz.cleangone.data.aws.dynamo.entity.action.Action;
import xyz.cleangone.data.aws.dynamo.entity.item.CartItem;
import xyz.cleangone.data.aws.dynamo.entity.organization.EventParticipant;
import xyz.cleangone.data.aws.dynamo.entity.organization.OrgEvent;
import xyz.cleangone.data.aws.dynamo.entity.organization.Organization;
import xyz.cleangone.data.aws.dynamo.entity.person.User;
import xyz.cleangone.data.aws.dynamo.entity.action.ActionType;
import xyz.cleangone.data.aws.dynamo.entity.purchase.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class ActionManager
{
    private final ActionDao actionDao = new ActionDao();
    private final Organization org;

    public ActionManager(Organization org)
    {
        this.org = requireNonNull(org);
    }

    public List<Action> getActionsByTargetEvent(String targetEventId)
    {
        return actionDao.getByTargetEventId(targetEventId);
    }

    public List<Action> getActionsBySourcePerson(String sourcePersonId)
    {
        return actionDao.getBySourcePersonId(sourcePersonId);
    }

    public List<Action> getActionsBySourcePerson(String sourcePersonId, String eventId)
    {
        List<Action> actions = actionDao.getBySourcePersonId(sourcePersonId);

        return actions.stream()
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

    private Action createDonation(User user, CartItem item)
    {
        return new Action(org.getId(), ActionType.Donated, item.getEvent(), user, item.getPrice())
            .withTargetPerson(item.getTargetParticipant());
    }

    private Action createPledgeFulfillment(User user, CartItem item)
    {
        return new Action(org.getId(), ActionType.FulfilledPledge, item.getEvent(), user, item.getPrice())
            .withDescription(item.getPledgeFulfillmentActionDesc())
            .withTargetPerson(requireNonNull(item.getTargetParticipant()));
    }

    private Action createPurchase(User user, CartItem item)
    {
        return new Action(org.getId(), ActionType.Purchased, item.getEvent(), user, item.getPrice())
            .withDescription(item.getCatalogItem().getName());
    }

    public Action createPledge(User user, BigDecimal amount, String description, OrgEvent targetEvent, EventParticipant targetParticipant)
    {
        return new Action(org.getId(), ActionType.Pledged, targetEvent, user)
            .withIterationAmount(amount)
            .withDescription(description)
            .withTargetPerson(requireNonNull(targetParticipant));
    }

    public static BigDecimal sumAmount(List<Action> actions)
    {
        return actions.stream()
            .filter(Action::hasAmount)
            .map(Action::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void save(Action action)
    {
        actionDao.save(action);
    }
}
