/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.l10n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry.form.IPropertySelectionModel;

/**
 *  A model that presents as a list all the available locales
 *  except the current one.
 *
 *  @author Howard Lewis Ship
 */
public class LocaleModel implements IPropertySelectionModel
{
    private Locale _activeLocale;
    private List _locales = new ArrayList();

    private static final Locale[] AVAILABLE_LOCALES =
        { Locale.ENGLISH, Locale.FRENCH, new Locale("es"), Locale.GERMAN };

    public LocaleModel(Locale activeLocale)
    {
        _activeLocale = activeLocale;

        String activeLanguage = activeLocale.getLanguage();

        for (int i = 0; i < AVAILABLE_LOCALES.length; i++)
        {
            if (AVAILABLE_LOCALES[i].getLanguage().equals(activeLanguage))
                continue;

            _locales.add(AVAILABLE_LOCALES[i]);
        }
    }

    public int getOptionCount()
    {
        return _locales.size();
    }

    public Object getOption(int index)
    {
        return _locales.get(index);
    }

    public String getLabel(int index)
    {
        Locale l = (Locale) _locales.get(index);

        return l.getDisplayLanguage(_activeLocale);
    }

    public String getValue(int index)
    {
        return Integer.toString(index);
    }

    public Object translateValue(String value)
    {
        int index = Integer.parseInt(value);

        return getOption(index);
    }

}
