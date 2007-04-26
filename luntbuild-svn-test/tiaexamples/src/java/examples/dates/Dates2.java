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
 * Example of integrating DatePicker components with field validation.
 * 
 * @author Howard M. Lewis Ship
 */
public abstract class Dates2 extends BasePage
{
    public abstract Date getStartDate();
    public abstract Date getEndDate();

    public void formSubmit(IRequestCycle cycle)
    {
        IValidationDelegate delegate = (IValidationDelegate) getBeans().getBean("delegate");

        Date startDate = getStartDate();
        Date endDate = getEndDate();

        if (startDate == null)
            error(delegate, "inputStart", "Start Date is required.", ValidationConstraint.REQUIRED);

        if (endDate == null)
            error(delegate, "inputEnd", "End Date is required.", ValidationConstraint.REQUIRED);

        if (delegate.getHasErrors())
            return;

        if (startDate.after(endDate))
        {
            error(
                delegate,
                "inputEnd",
                "End Date must be after Start Date.",
                ValidationConstraint.CONSISTENCY);
            return;
        }

        DatesConfirm next = (DatesConfirm) cycle.getPage("DatesConfirm");
        next.setStartDate(startDate);
        next.setEndDate(endDate);
        cycle.activate(next);
    }

    private void error(
        IValidationDelegate delegate,
        String componentId,
        String message,
        ValidationConstraint constraint)
    {
        IFormComponent component = (IFormComponent) getComponent(componentId);

        delegate.setFormComponent(component);
        delegate.record(message, constraint);
    }
}