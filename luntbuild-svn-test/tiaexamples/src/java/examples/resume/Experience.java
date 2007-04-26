/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.resume;

import org.apache.commons.lang.enum.Enum;

/**
 * Enumeration of different levels of experience.  Used on the
 * {@link examples.resume.ResumeBuilder} page.
 * 
 * @author Howard Lewis Ship
 */
public class Experience extends Enum
{
    public static final Experience NEWBIE = new Experience("NEWBIE");
    public static final Experience JUNIOR = new Experience("JUNIOR");
    public static final Experience ASSOCIATE = new Experience("ASSOCIATE");
    public static final Experience SENIOR = new Experience("SENIOR");

    public static final Experience[] ALL_VALUES = { NEWBIE, JUNIOR, ASSOCIATE, SENIOR };

    private Experience(String name)
    {
        super(name);
    }

}
