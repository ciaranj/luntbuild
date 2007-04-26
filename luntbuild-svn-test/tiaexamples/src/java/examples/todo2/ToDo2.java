/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.todo2;

import org.apache.tapestry.form.IPropertySelectionModel;

import examples.todo1.ToDo;
import examples.todo1.ToDoItem;

/**
 *  Extends {@link examples.todo1.ToDo}, providing
 *  a {@link org.apache.tapestry.form.IPropertySelectionModel model}
 *  for editing {@link examples.todo2.ToDoItem2#getPriority() priority}.
 *
 *  @author Howard Lewis Ship
 */
public abstract class ToDo2 extends ToDo
{
    public IPropertySelectionModel getPriorityModel()
    {
        return new PriorityModel();
    }

    protected ToDoItem createNewItem(String title)
    {
        return new ToDoItem2(title);
    }

}
