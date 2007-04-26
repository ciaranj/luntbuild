/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.resume;

import java.util.List;

/**
 * Data object editted by the {@link examples.resume.ResumeBuilder} page.
 * 
 * @author Howard Lewis Ship
 */
public class Resume
{
	private Experience _experience;
	private List _languages;
	
    public Experience getExperience()
    {
        return _experience;
    }

    public List getLanguages()
    {
        return _languages;
    }

    public void setExperience(Experience experience)
    {
        _experience = experience;
    }

    public void setLanguages(List languages)
    {
        _languages = languages;
    }

}
