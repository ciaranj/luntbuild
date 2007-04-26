/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.register;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.valid.BaseValidator;
import org.apache.tapestry.valid.ValidatorException;

/**
 * Implementation of {@link org.apache.tapestry.valid.IValidator} that allows
 * a field to be validated against a regular expression.
 * 
 * @author Howard Lewis Ship
 */
public class PatternValidator extends BaseValidator
{
    private String _pattern;
    private Pattern _compiledPattern;
    private String _errorMessage;
    private Perl5Matcher _matcher;

    public String toString(IFormComponent field, Object value)
    {
        if (value == null)
            return null;

        return value.toString();
    }

    public Object toObject(IFormComponent field, String input) throws ValidatorException
    {
        if (checkRequired(field, input))
            return null;

        if (!match(input))
            throw new ValidatorException(_errorMessage, null);

        return input;
    }

    protected boolean match(String input)
    {
        if (_compiledPattern == null)
        {
            PatternCompiler compiler = new Perl5Compiler();

            try
            {
                _compiledPattern = compiler.compile(_pattern);
            }
            catch (MalformedPatternException ex)
            {
                throw new ApplicationRuntimeException(ex);
            }
        }

        if (_matcher == null)
            _matcher = new Perl5Matcher();

        return _matcher.matches(input, _compiledPattern);
    }

    public String getErrorMessage()
    {
        return _errorMessage;
    }

    public String getPattern()
    {
        return _pattern;
    }

    public void setErrorMessage(String errorMessage)
    {
        _errorMessage = errorMessage;
    }

    public void setPattern(String pattern)
    {
        _pattern = pattern;
        _compiledPattern = null;
    }

}
