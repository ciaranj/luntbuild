/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.cc;

import org.apache.tapestry.html.BasePage;

/**
 * Page activated to display the input collected on the
 * {@link CreditCard} page.
 * 
 * @author Howard Lewis Ship
 */
public abstract class CreditCardResults extends BasePage
{
    public abstract void setCardNumber(String cardNumber);
    public abstract void setCardType(CreditCardType type);
}
