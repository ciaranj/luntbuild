/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.cc;

import org.apache.commons.lang.enum.Enum;

/**
 * Enumeration of different types of credit cards.
 * 
 * @author Howard Lewis Ship
 */
public class CreditCardType extends Enum
{
	public static final CreditCardType MASTERCARD = new CreditCardType("MASTERCARD");
	public static final CreditCardType VISA = new CreditCardType("VISA");

    public CreditCardType(String name)
    {
        super(name);
    }

}
