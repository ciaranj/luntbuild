/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.todo2;

import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * A PropertySelection model that allows the priority of a
 * {@link examples.todo2.ToDoItem2} to be editted.
 * 
 * @author Howard Lewis Ship
 */
public class PriorityModel implements IPropertySelectionModel
{
    public int getOptionCount()
    {
        return 3;
    }

    public Object getOption(int index)
    {
        switch (index)
        {
            case 0 :
                return new Integer(ToDoItem2.HIGH_PRIORITY);

            case 1 :
                return new Integer(ToDoItem2.MEDIUM_PRIORITY);

            default :
                return new Integer(ToDoItem2.LOW_PRIORITY);
        }
    }

    public String getLabel(int index)
    {
        switch (index)
        {
            case 0 :
                return "High";

            case 1 :
                return "Medium";

            default :

                return "Low";
        }
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
