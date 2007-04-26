/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.resume;

import java.util.*;

import org.apache.tapestry.html.BasePage;

/**
 * Activated from the {@link examples.resume.ResumeBuilder} page
 * to parrot back what the user has entered.
 * 
 * @author Howard Lewis Ship
 */
public abstract class ResumeSummary extends BasePage
{
    public abstract void setResume(Resume resume);
    public abstract Resume getResume();

    private ResourceBundle _experienceBundle;
    private ResourceBundle _languageBundle;

    public void finishLoad()
    {
        _experienceBundle = getBundle("ExperienceStrings");
        _languageBundle = getBundle("LanguageStrings");
    }

    public ResourceBundle getBundle(String name)
    {
        return ResourceBundle.getBundle(
            getClass().getPackage().getName() + "." + name,
            getLocale());
    }

    public String getExperience()
    {
        Experience experience = getResume().getExperience();

        return _experienceBundle.getString(experience.getName());
    }

    public String getLanguages()
    {
        StringBuffer buffer = new StringBuffer();
        List languages = getResume().getLanguages();
        int count = languages.size();

        for (int i = 0; i < count; i++)
        {
            if (i + 1 == count)
                buffer.append(" and ");
            else
                if (i > 0)
                    buffer.append(", ");

            Language language = (Language) languages.get(i);

            buffer.append(_languageBundle.getString(language.getName()));
        }

        return buffer.toString();
    }

}
