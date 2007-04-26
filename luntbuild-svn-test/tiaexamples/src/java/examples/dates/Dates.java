/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.dates;

import java.util.Date;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.valid.IValidationDelegate;
import org.apache.tapestry.valid.ValidationConstraint;

/**
 * Demonstrates how to perform cross-form validation consistency checks; here
 * the start date must precede the end date.
 * 
 * @author Howard Lewis Ship
 */
public abstract class Dates extends BasePage
{
    public abstract Date getStartDate();
    public abstract Date getEndDate();

    public void formSubmit(IRequestCycle cycle)
    {
        IValidationDelegate delegate = (IValidationDelegate) getBeans().getBean("delegate");

        if (delegate.getHasErrors())
            return;

        Date startDate = getStartDate();
        Date endDate = getEndDate();

        if (startDate.after(endDate))
        {
            IFormComponent inputEnd = (IFormComponent) getComponent("inputEnd");
            delegate.setFormComponent(inputEnd);
            delegate.record("End Date must be after Start Date.", ValidationConstraint.CONSISTENCY);
            return;
        }
        
        DatesConfirm next = (DatesConfirm)cycle.getPage("DatesConfirm");
        next.setStartDate(startDate);
        next.setEndDate(endDate);
        cycle.activate(next);
    }
}
