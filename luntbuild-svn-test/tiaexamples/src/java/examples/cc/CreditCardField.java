/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.cc;

import java.util.HashMap;
import java.util.Map;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IForm;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.IResourceLocation;
import org.apache.tapestry.IScript;
import org.apache.tapestry.engine.IScriptSource;
import org.apache.tapestry.form.AbstractFormComponent;
import org.apache.tapestry.form.FormEventType;
import org.apache.tapestry.html.Body;
import org.apache.tapestry.request.RequestContext;
import org.apache.tapestry.valid.IValidationDelegate;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidatorException;

/**
 * Renders as a set of four input fields for entering
 * a common credit-card number.
 *
 * @author Howard Lewis Ship
 */
public abstract class CreditCardField extends AbstractFormComponent
{
    private IScript _script;
    private Pattern _compiledPattern;
    private Perl5Matcher _matcher;

    public abstract void setCardNumber(String cardNumber);
    public abstract String getCardNumber();

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        IForm form = getForm(cycle);

        // Get the base name; we extend this to create four seperate query parameters.

        String name = form.getElementId(this);
        boolean disabled = isDisabled();
        IValidationDelegate delegate = form.getDelegate();

        if (form.isRewinding())
        {
            if (!disabled)
                updateCardNumberFromRequest(name, form, delegate, cycle);

            return;
        }

        // If the page is rewinding, but the form isn't,
        // then no point in doing anything else.

        if (cycle.isRewinding())
            return;

        String cardNumber[] = extractCardNumber(delegate);

		delegate.writePrefix(writer, cycle, this, null);
		
        for (int i = 0; i < 4; i++)
        {
            if (i > 0)
                writer.print(" - ");

            // $ is a good separator because its not allowed as a normal component id,
            // so there's no chance of conflict.

            String fieldName = name + "$g" + i;

            writer.beginEmpty("input");
            writer.attribute("type", "text");
            writer.attribute("name", fieldName);
            writer.attribute("size", 4);
            writer.attribute("maxlength", 4);
            writer.attribute("value", cardNumber[i]);

            if (disabled)
                writer.attribute("disabled", "disabled");

            // The delegate updates *each* of the rendered text fields.

            delegate.writeAttributes(writer, cycle, this, null);
        }
		
		delegate.writeSuffix(writer, cycle, this, null);
		
        if (!disabled)
        {
            // Now run the script.

            if (_script == null)
            {
                IScriptSource source = cycle.getEngine().getScriptSource();
                IResourceLocation specLocation =
                    getSpecification().getLocation().getResourceLocation();
                IResourceLocation scriptLocation =
                    specLocation.getRelativeLocation("CreditCardField.script");

                _script = source.getScript(scriptLocation);
            }

			Body body = Body.get(cycle);

            Map symbols = new HashMap();
            symbols.put("creditCardField", this);
            symbols.put("formatMessage", getMessage("card-number-format"));

            _script.execute(cycle, body, symbols);

            String formValidateFunction = (String) symbols.get("formValidateFunction");

            form.addEventHandler(FormEventType.SUBMIT, formValidateFunction);
        }
    }

    private void updateCardNumberFromRequest(
        String name,
        IForm form,
        IValidationDelegate delegate,
        IRequestCycle cycle)
    {
        RequestContext context = cycle.getRequestContext();

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 4; i++)
        {
            String value = context.getParameter(name + "$g" + i);

            if (value != null)
            {
                // This shouldn't happen because we set a maxlength
                // for the fields.

                if (value.length() > 4)
                    value = value.substring(0, 4);

                buffer.append(value);
            }

            // This shouldn't happen, because the client-side
            // script checks that each field has exactly
            // the right number of digits.

            while (buffer.length() < 4 * (i + 1))
                buffer.append(' ');
        }

        String cardNumber = buffer.toString();

        delegate.recordFieldInputValue(cardNumber);

        try
        {
            validate(cardNumber);
            setCardNumber(cardNumber);
        }
        catch (ValidatorException ex)
        {
            delegate.record(ex);
        }

    }

    private String[] extractCardNumber(IValidationDelegate delegate)
    {
        String cardNumber = delegate.getFieldInputValue();

        if (cardNumber == null)
            cardNumber = getCardNumber();

        if (cardNumber == null)
            return new String[] { "", "", "", "" };

        String[] result = new String[4];
        StringBuffer buffer = new StringBuffer(cardNumber);

        while (buffer.length() < 16)
            buffer.append(' ');

        for (int i = 0; i < 4; i++)
            result[i] = buffer.substring(4 * i, 4 * (i + 1));

        return result;
    }

    private void validate(String cardNumber) throws ValidatorException
    {
        if (_compiledPattern == null)
        {
            Perl5Compiler compiler = new Perl5Compiler();
            try
            {
                _compiledPattern = compiler.compile("^\\d{16}$");
            }
            catch (MalformedPatternException ex)
            {
                throw new ApplicationRuntimeException(ex);
            }
        }

        if (_matcher == null)
            _matcher = new Perl5Matcher();

        if (_matcher.matches(cardNumber, _compiledPattern))
            return;

        String formatted =
            cardNumber.substring(0, 4)
                + "-"
                + cardNumber.substring(4, 8)
                + "-"
                + cardNumber.substring(8, 12)
                + "-"
                + cardNumber.substring(12);

        throw new ValidatorException(
            format("invalid-card-number", formatted),
            ValidationConstraint.NUMBER_FORMAT);
    }
}
