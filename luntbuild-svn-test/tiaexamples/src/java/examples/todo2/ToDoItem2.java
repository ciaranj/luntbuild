/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.todo2;

import examples.todo1.ToDoItem;

/**
 *  Extends {@link examples.todo1.ToDoItem}
 *  with a priority level.
 *
 *  @author Howard Lewis Ship
 */
public class ToDoItem2 extends ToDoItem
{
    public static final int LOW_PRIORITY = 100;
    public static final int MEDIUM_PRIORITY = 200;
    public static final int HIGH_PRIORITY = 300;

    private int _priority = MEDIUM_PRIORITY;

    public ToDoItem2()
    {
    }

    public ToDoItem2(String title)
    {
        super(title);
    }

    public int getPriority()
    {
        return _priority;
    }

    public void setPriority(int priority)
    {
        _priority = priority;
    }

}
