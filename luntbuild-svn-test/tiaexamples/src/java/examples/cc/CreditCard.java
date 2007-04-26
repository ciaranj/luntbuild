/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.cc;

import java.util.ResourceBundle;

import org.apache.commons.lang.enum.Enum;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.EnumPropertySelectionModel;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.valid.IValidationDelegate;

/**
 * Demonstrates the use of the {@link examples.cc.CreditCardField} component.
 * 
 * @author Howard Lewis Ship
 */
public abstract class CreditCard extends BasePage
{
    private IPropertySelectionModel _cardModel;

    public abstract String getCardNumber();
    public abstract CreditCardType getCardType();

    public IPropertySelectionModel getCardModel()
    {
        if (_cardModel == null)
        {
            _cardModel =
                new EnumPropertySelectionModel(
                    new Enum[] { CreditCardType.MASTERCARD, CreditCardType.VISA },
                    ResourceBundle.getBundle("examples.cc.CreditCardStrings", getLocale()));
        }

        return _cardModel;
    }

    public void formSubmit(IRequestCycle cycle)
    {
        IValidationDelegate delegate = (IValidationDelegate) getBeans().getBean("delegate");

        if (delegate.getHasErrors())
            return;

        CreditCardResults results = (CreditCardResults) cycle.getPage("CreditCardResults");
        results.setCardNumber(getCardNumber());
        results.setCardType(getCardType());

        cycle.activate(results);
    }
}
