/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.todo3;

import org.apache.commons.lang.enum.Enum;

/**
 *  Enums for the different possible priority levels.
 *
 *  @author Howard Lewis Ship
 */
public class Priority extends Enum
{
    public static final Priority HIGH = new Priority("HIGH");
    public static final Priority MEDIUM = new Priority("MEDIUM");
    public static final Priority LOW = new Priority("LOW");

    public static final Priority[] ALL_VALUES = { HIGH, MEDIUM, LOW };

    private Priority(String name)
    {
        super(name);
    }

}
