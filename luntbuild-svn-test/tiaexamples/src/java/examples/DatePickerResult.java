/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples;

import java.text.DateFormat;
import java.util.Date;

import org.apache.tapestry.html.BasePage;

/**
 * Result page activated by {@link examples.DatePicker#formSubmit(IRequestCycle)}.
 * 
 * @author Howard Lewis Ship
 */
public abstract class DatePickerResult extends BasePage
{
    public abstract void setDate(Date date);

    public DateFormat getDateFormat()
    {
        return DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
    }
}
