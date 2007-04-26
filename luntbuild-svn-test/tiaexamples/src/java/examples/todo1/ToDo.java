/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.todo1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageRenderListener;
import org.apache.tapestry.html.BasePage;

/**
 * Edits a list of {@link ToDo} items. Demonstrates how to build a complex form, one
 * which edits a list of data items as a single submission. This class serves as a base class
 * for several improved implementations.
 * 
 * @author Howard Lewis Ship
 */
public abstract class ToDo extends BasePage implements PageRenderListener
{
    public abstract List getToDoList();
    public abstract void setToDoList(List toDoList);

    public abstract ToDoItem getMoveUpItem();

    public abstract ToDoItem getMoveDownItem();

    public void addTodoItem(IRequestCycle cycle)
    {
        getToDoList().add(createNewItem("New Item"));
    }

	/**
	 * Creates a new instance of {@link ToDoItem}. Subclasses of this page class
	 * override this method to create new instances of the corresponding
	 * {@link ToDoItem} subclass.
	 */
	protected ToDoItem createNewItem(String title)
	{
		return new ToDoItem(title);
	}

    // The Submit for this is after the main loop, so its safe to
    // actually edit the list.

    public void deleteCompleted(IRequestCycle cycle)
    {
        ListIterator i = getToDoList().listIterator();

        while (i.hasNext())
        {
            ToDoItem item = (ToDoItem) i.next();

            if (item.isCompleted())
                i.remove();
        }
    }

    public void formSubmit(IRequestCycle cycle)
    {
        List list = getToDoList();

        int count = list.size();
        ToDoItem moveUpItem = getMoveUpItem();
        ToDoItem moveDownItem = getMoveDownItem();

        for (int i = 0; i < count; i++)
        {
            ToDoItem item = (ToDoItem) list.get(i);

            if (item == moveUpItem)
            {
                if (i > 0)
                    Collections.swap(list, i, i - 1);

                break;
            }

            if (item == moveDownItem)
            {
                if (i + 1 < count)
                    Collections.swap(list, i, i + 1);

                break;
            }
        }

        // Always important to set peristent properties; otherwise changes
        // made in this request cycle will be lost.  The framework
        // makes a copy of the list.

        setToDoList(list);
    }

    public void pageBeginRender(PageEvent event)
    {
        List list = getToDoList();

        if (list == null)
        {
            list = new ArrayList();
            list.add(createNewItem("Finish reading Tapestry Book"));
            list.add(createNewItem("Download latest version of Tapestry"));

            setToDoList(list);
        }
    }

}
