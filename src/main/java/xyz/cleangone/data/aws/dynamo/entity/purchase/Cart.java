package xyz.cleangone.data.aws.dynamo.entity.purchase;

import xyz.cleangone.data.aws.dynamo.entity.item.CartItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart
{
    private List<CartItem> items = new ArrayList<>();

    private BigDecimal total;
    private boolean containsDonations;
    private boolean containsPurchases;

    private String returnPage;


    public Cart(String returnPage)
    {
        this.returnPage = returnPage;
        setContains();
    }

    public void clear()
    {
        items.clear();
        setContains();
    }

//    public boolean containsItem(CartItem item)
//    {
//        if (item instanceof OLDPledgeFulfillmentCartItem) { return containsItem((OLDPledgeFulfillmentCartItem)item); }
//        return false;
//    }
//
//    private boolean containsItem(OLDPledgeFulfillmentCartItem item)
//    {
//        for (CartItem existingItem : items)
//        {
//            if (existingItem instanceof OLDPledgeFulfillmentCartItem &&
//                ((OLDPledgeFulfillmentCartItem) existingItem).getPledge().getId().equals(item.getPledge().getId()))
//            {
//                return true;
//            }
//        }
//
//        return false;
//    }

    public String getButtonCaption()
    {
        return (containsDonations ? "Donate" :  "") +
            (containsDonations && containsPurchases  ? "/" :  "") +
            (containsPurchases ? "Purchase" :  "");
    }

    public String getSuccessMsg()
    {
        return (containsDonations ? "Donations" :  "") +
            (containsDonations && containsPurchases  ? " and " :  "") +
            (containsPurchases ? "Purchases" :  "") +
            " made";
    }

    public BigDecimal getTotal()
    {
        return total;
    }

    public String getDisplayTotal()
    {
        return "$" + total;
    }

    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    public List<CartItem> getItems()
    {
        return items;
    }

    public void addItem(CartItem item)
    {
        if (addNewItem(item)) { setContains(item); }
    }

    public void addItems(List<CartItem> items)
    {
        boolean cartUpdated = false;

        for (CartItem item : items)
        {
            if (addNewItem(item)) { cartUpdated = true; }
        }

        if (cartUpdated) { setContains(); }
    }

    public boolean addNewItem(CartItem item)
    {
//        if (containsItem(item)) { return false; }

        items.add(item);
        return true;
    }

    public void removeItem(CartItem item)
    {
        items.remove(item);
        setContains();
    }

    private void setContains()
    {
        total = new BigDecimal(0);
        containsDonations = false;
        containsPurchases = false;

        items.forEach(item -> setContains(item));
    }

    private void setContains(CartItem item)
    {
        total = total.add(item.getPrice());

//        if (item instanceof OLDDonationCartItem) { containsDonations = true; }
//        else {

            containsPurchases = true;
//        }
    }

    public String getReturnPage()
    {
        return returnPage;
    }
    public void setReturnPage(String returnPage)
    {
        this.returnPage = returnPage;
    }
}


