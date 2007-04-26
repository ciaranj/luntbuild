/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples;

import java.util.Date;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;

/**
 * Page used to demonstrate the DatePicker component.
 * 
 * @author Howard Lewis Ship
 */
public abstract class DatePicker extends BasePage
{
	public abstract Date getDate();
	
	public void formSubmit(IRequestCycle cycle)
	{
		DatePickerResult page = (DatePickerResult)cycle.getPage("DatePickerResult");
		page.setDate(getDate());
		cycle.activate(page);
	}
}
