/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;

/**
 * Page invoked from a JSP using the &lt;tapestry:external-url&gt; JSP tag.
 * Demonstrates how a Tapestry page can receive a form submission from a JSP.
 * 
 * @author Howard Lewis Ship
 */
public abstract class JSPForm extends BasePage implements IExternalPage
{
    protected abstract void setUserName(String userName);

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle)
    {
        String userName = cycle.getRequestContext().getParameter("userName");
        setUserName(userName);
    }

}
