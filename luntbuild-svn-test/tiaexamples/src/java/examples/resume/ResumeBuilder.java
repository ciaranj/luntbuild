/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.resume;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.form.EnumPropertySelectionModel;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.valid.IValidationDelegate;
import org.apache.tapestry.valid.ValidationConstraint;

/**
 * Page used to enter resume information; this serves as a demonstration
 * of the Palette component.
 * 
 * @author Howard Lewis Ship
 */
public abstract class ResumeBuilder extends BasePage
{
    private IPropertySelectionModel _experienceModel;
    private IPropertySelectionModel _languageModel;

    public IPropertySelectionModel getExperienceModel()
    {
        if (_experienceModel == null)
            _experienceModel =
                new EnumPropertySelectionModel(
                    Experience.ALL_VALUES,
                    getBundle("ExperienceStrings"));

        return _experienceModel;
    }

    public IPropertySelectionModel getLanguageModel()
    {
        if (_languageModel == null)
            _languageModel =
                new EnumPropertySelectionModel(Language.ALL_VALUES, getBundle("LanguageStrings"));

        return _languageModel;
    }

    public ResourceBundle getBundle(String name)
    {
        return ResourceBundle.getBundle(
            getClass().getPackage().getName() + "." + name,
            getLocale());
    }

    public void formSubmit(IRequestCycle cycle)
    {
        List languages = getLanguages();

        if (Tapestry.size(languages) == 0)
        {
            IFormComponent inputLanguages = (IFormComponent) getComponent("inputLanguages");
            IValidationDelegate delegate = (IValidationDelegate) getBeans().getBean("delegate");

            delegate.setFormComponent(inputLanguages);
            delegate.record(
                "You must supply at least one language proficiency.",
                ValidationConstraint.REQUIRED);

            return;
        }
        
        Resume resume = new Resume();
        resume.setLanguages(languages);
        resume.setExperience(getExperience());
        
        ResumeSummary next = (ResumeSummary)cycle.getPage("ResumeSummary");
        next.setResume(resume);
        cycle.activate(next);
    }

    public abstract List getLanguages();
    public abstract Experience getExperience();
}
