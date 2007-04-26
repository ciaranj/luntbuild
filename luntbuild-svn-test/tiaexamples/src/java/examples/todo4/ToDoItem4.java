/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.todo4;

import examples.todo3.ToDoItem3;

/**
 * Adds a unique id property to {@link examples.todo3.ToDoItem3}, so that
 * the items may be managed by a {@link org.apache.tapestry.form.ListEditMap}.
 * 
 * @author Howard Lewis Ship
 */
public class ToDoItem4 extends ToDoItem3
{
	private static int _nextuid = 0;
	
    private int _uid = _nextuid++;


    public ToDoItem4()
    {
        super();
    }

    public ToDoItem4(String title)
    {
        super(title);
    }

    public int getUid()
    {
        return _uid;
    }
}
