/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.pets;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;

/**
 * Demonstration of the use of the {@link examples.DirectArea} component.
 * 
 * @author Howard Lewis Ship
 */
public class Pets extends BasePage
{
    private void select(String type)
    {
        IRequestCycle cycle = getRequestCycle();

        PetCategory next = (PetCategory) cycle.getPage("PetCategory");

        next.setType(type);
        cycle.activate(next);
    }

    public void selectBirds(IRequestCycle cycle)
    {
        select("birds");
    }

    public void selectCats(IRequestCycle cycle)
    {
        select("cats");
    }

    public void selectDogs(IRequestCycle cycle)
    {
        select("dogs");
    }

    public void selectFish(IRequestCycle cycle)
    {
        select("fish");
    }

    public void selectReptiles(IRequestCycle cycle)
    {
        select("reptiles");
    }
}
