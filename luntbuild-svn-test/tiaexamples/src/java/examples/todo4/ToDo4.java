/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.todo4;

import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.ListEditMap;

import examples.todo1.ToDoItem;
import examples.todo3.ToDo3;

/**
 * Improved version of {@link examples.todo3.ToDo3} that uses a
 * ListEdit component (and {@link org.apache.tapestry.form.ListEditMap}) to
 * avoid stale links caused by out of date form submissions.
 * 
 * @author Howard Lewis Ship
 */
public abstract class ToDo4 extends ToDo3
{
    public abstract void setErrorMessage(String message);

    public abstract void setListEditMap(ListEditMap listEditMap);
    public abstract ListEditMap getListEditMap();

    public abstract void setItem(ToDoItem item);

    protected ToDoItem createNewItem(String title)
    {
        return new ToDoItem4(title);
    }

    public void pageBeginRender(PageEvent event)
    {
        super.pageBeginRender(event);

        // Construct a ListEditMap with all the current items.

        ListEditMap map = new ListEditMap();

        List items = getToDoList();
        int count = items.size();

        for (int i = 0; i < count; i++)
        {
            ToDoItem4 item = (ToDoItem4) items.get(i);
            int uid = item.getUid();

            map.add(new Integer(uid), item);
        }

        setListEditMap(map);
    }

    public void synchronizeItem(IRequestCycle cycle)
    {
        ListEditMap map = getListEditMap();

        ToDoItem item = (ToDoItem) map.getValue();

        if (item == null)
        {
            setErrorMessage("Your form submission is out of date.  Please retry.");
            throw new PageRedirectException(this);
        }

        setItem(item);
    }
}
