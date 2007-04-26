/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.register;

import org.apache.tapestry.html.BasePage;

/**
 * Page activated by {@link examples.register.Register} to 
 * display the input {@link examples.register.Address}.
 * 
 * @author Howard Lewis Ship
 */
public abstract class RegisterConfirm extends BasePage
{
	public abstract void setAddress(Address address);
}
