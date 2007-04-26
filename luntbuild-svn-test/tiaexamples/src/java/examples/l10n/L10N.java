/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.l10n;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.html.BasePage;

/**
 * Demonstrates changing of the engine's locale.
 * 
 * @author Howard Lewis Ship
 */
public class L10N extends BasePage
{
    private IPropertySelectionModel _localeModel;

    public IPropertySelectionModel getLocaleModel()
    {
        if (_localeModel == null)
            _localeModel = new LocaleModel(getLocale());

        return _localeModel;
    }

    public void formSubmit(IRequestCycle cycle)
    {
        // This page is already loaded, with whatever the old locale was.
        // engine.locale has been updated with the new locale.
        // Load and render a different page to see the change.

        cycle.activate("L10NResult");
    }
}
