/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.dates;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tapestry.html.BasePage;

/**
 * Displays a start date and an end date. Activated by {@link examples.dates.Dates} and
 * {@link examples.dates.Dates2}.
 * 
 * @author Howard Lewis Ship
 */
public abstract class DatesConfirm extends BasePage
{
	public abstract void setEndDate(Date endDate);
	public abstract void setStartDate(Date startDate);
	
	public DateFormat getDateFormat()
	{
		return SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, getLocale());
	}
}
