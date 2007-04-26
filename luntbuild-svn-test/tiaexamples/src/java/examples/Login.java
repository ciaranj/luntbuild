/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;

/**
 *  Demonstration of very simple form support.
 *
 *  @author Howard Lewis Ship
 */
public abstract class Login extends BasePage
{
    // Abstract accessors provided by Tapestry in enhanced
    // subclass.  We only declare the accessors we need in code,
    // but the properties are always read/write.

    public abstract String getUserName();
    public abstract String getPassword();
    public abstract void setMessage(String message);

    public void login(IRequestCycle cycle)
    {
        if (isValidLogin(getUserName(), getPassword()))
        {
            cycle.activate("Main");
            return;
        }

        setMessage("Invalid user name or password.");
    }

    private boolean isValidLogin(String userName, String password)
    {
        return "tapestry".equalsIgnoreCase(userName);
    }

}
