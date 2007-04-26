/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples;

import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IDirect;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.link.DirectLink;

/**
 * Component used with image maps.  Renders renders an &lt;area&gt; element (as part of
 * a &lt;map&gt;) that uses the direct service to invoke a listener method.
 * 
 * @author Howard Lewis Ship
 */
public abstract class DirectArea extends AbstractComponent implements IDirect
{
    public abstract IActionListener getListener();
    public abstract Object getParameters();

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        if (cycle.isRewinding())
            return;

        Object[] parameters = DirectLink.constructServiceParameters(getParameters());

        IEngineService service = cycle.getEngine().getService(Tapestry.DIRECT_SERVICE);
        ILink link = service.getLink(cycle, this, parameters);

        writer.beginEmpty("area");
        writer.attribute("href", link.getURL());

        renderInformalParameters(writer, cycle);
    }

    public void trigger(IRequestCycle cycle)
    {
        IActionListener listener = getListener();

        if (listener == null)
            throw Tapestry.createRequiredParameterException(this, "listener");

        listener.actionTriggered(this, cycle);
    }

    public boolean isStateful()
    {
        return false;
    }

}
