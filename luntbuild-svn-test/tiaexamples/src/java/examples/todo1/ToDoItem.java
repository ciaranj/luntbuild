/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.todo1;

import java.io.Serializable;

/**
 * Data object editted on the {@link examples.todo1.ToDo} page. Several
 * subclasses add additional properties.
 * 
 * @author Howard Lewis Ship
 */
public class ToDoItem implements Serializable
{
    private boolean _completed;
    private String _title;

    public ToDoItem()
    {
    }

    public ToDoItem(String title)
    {
        _title = title;
    }

    public boolean isCompleted()
    {
        return _completed;
    }

    public String getTitle()
    {
        return _title;
    }

    public void setCompleted(boolean completed)
    {
        _completed = completed;
    }

    public void setTitle(String title)
    {
        _title = title;
    }

}
