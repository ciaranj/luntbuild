/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.html.BasePage;

/**
 * Page that demonstrates Tapestry/JSP interaction. Links on the page will
 * cause a JSP to render the response (rather than activating a Tapestry page
 * to render the response).
 * 
 * @author Howard Lewis Ship
 */
public class JSP extends BasePage
{
	public void displayPageTagDemo(IRequestCycle cycle)
	{
		throw new RedirectException("PageTagDemo.jsp");
	}
	
	public void displayExternalTagDemo(IRequestCycle cycle)
	{
		throw new RedirectException("ExternalTagDemo.jsp");
	}
}
