/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageRenderListener;
import org.apache.tapestry.html.BasePage;

/**
 * Demonstrates how to use the Select and Option components to create a multiple-selection
 * list.
 * 
 * @author Howard Lewis Ship
 */
public abstract class Toppings extends BasePage implements PageRenderListener
{
    private static String[] TOPPINGS =
        { "Lettuce", "Tomato", "Cheese", "Onions", "Pickles", "Relish", "Mustard", "Ketchup" };

    public String[] getAllToppings()
    {
        return TOPPINGS;
    }

    public abstract String getTopping();

    public abstract void setSelectedToppings(Set toppings);

    public abstract Set getSelectedToppings();

    public boolean isToppingSelected()
    {
        return getSelectedToppings().contains(getTopping());
    }

    public void setToppingSelected(boolean toppingSelected)
    {
        if (toppingSelected)
            getSelectedToppings().add(getTopping());
        else
            getSelectedToppings().remove(getTopping());
    }

    public void selectToppings(IRequestCycle cycle)
    {
        String toppings = getToppingsList();

        ToppingsResult page = (ToppingsResult) cycle.getPage("ToppingsResult");
        page.setToppings(toppings);
        cycle.activate(page);
    }

    private String getToppingsList()
    {
        if (getSelectedToppings().isEmpty())
            return "No toppings.";

        StringBuffer buffer = new StringBuffer();

        int count = getSelectedToppings().size();

        int x = 0;
        Iterator i = getSelectedToppings().iterator();

        while (i.hasNext())
        {
            if (++x > 1)
            {
                if (x == count)
                    buffer.append(" and ");
                else
                    buffer.append(", ");
            }

            String topping = (String) i.next();

            buffer.append(topping);
        }

        buffer.append(".");

        return buffer.toString();
    }

    public void pageBeginRender(PageEvent event)
    {
        setSelectedToppings(new HashSet());
    }

}
