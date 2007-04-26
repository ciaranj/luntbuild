/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.todo3;

import java.util.ResourceBundle;

import org.apache.tapestry.form.EnumPropertySelectionModel;
import org.apache.tapestry.form.IPropertySelectionModel;

import examples.todo1.ToDo;
import examples.todo1.ToDoItem;

/**
 * Subclass of {@link examples.todo1.ToDo} that supports editting the priority
 * of a {@link examples.todo3.ToDoItem3}.
 * 
 * @author Howard Lewis Ship
 */
public abstract class ToDo3 extends ToDo
{
    private IPropertySelectionModel _priorityModel;

    public IPropertySelectionModel getPriorityModel()
    {
        if (_priorityModel == null)
            _priorityModel = buildPriorityModel();

        return _priorityModel;
    }

    private IPropertySelectionModel buildPriorityModel()
    {
        ResourceBundle bundle =
            ResourceBundle.getBundle("examples.todo3.PriorityStrings", getLocale());

        return new EnumPropertySelectionModel(Priority.ALL_VALUES, bundle);
    }

    protected ToDoItem createNewItem(String title)
    {
        return new ToDoItem3(title);
    }

}
