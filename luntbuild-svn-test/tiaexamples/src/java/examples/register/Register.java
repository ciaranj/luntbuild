/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.register;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageRenderListener;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.valid.IValidationDelegate;

/**
 * Page used to demonstrate Tapestry's input validation subsystem, including
 * custom validators (such as {@link examples.register.PatternValidator}).
 * 
 * @uahtor Howard Lewis Ship
 */
public abstract class Register extends BasePage implements PageRenderListener
{
    public abstract Address getAddress();
    public abstract void setAddress(Address address);

    public void pageBeginRender(PageEvent event)
    {
        if (getAddress() == null)
            setAddress(new Address());
    }

    public void formSubmit(IRequestCycle cycle)
    {
        IValidationDelegate delegate = (IValidationDelegate) getBeans().getBean("delegate");

        if (delegate.getHasErrors())
            return;

        RegisterConfirm next = (RegisterConfirm) cycle.getPage("RegisterConfirm");

        next.setAddress(getAddress());
        cycle.activate(next);
    }

}
