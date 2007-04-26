/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.todo3;

import examples.todo1.ToDoItem;

/**
 * Alternate version of {@link examples.todo2.ToDoItem2} whose
 * priority property is an instance of {@link examples.todo3.Priority}, not
 * an int.
 * 
 * @author Howard Lewis Ship
 */
public class ToDoItem3 extends ToDoItem
{
    private Priority _priority = Priority.MEDIUM;

    public ToDoItem3()
    {
    }

    public ToDoItem3(String title)
    {
        super(title);
    }

    public Priority getPriority()
    {
        return _priority;
    }

    public void setPriority(Priority priority)
    {
        _priority = priority;
    }

}
