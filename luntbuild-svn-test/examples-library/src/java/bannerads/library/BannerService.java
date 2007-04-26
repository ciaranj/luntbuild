/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package bannerads.library;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.engine.AbstractService;
import org.apache.tapestry.engine.IEngineServiceView;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.request.ResponseOutputStream;
import org.apache.tapestry.spec.IApplicationSpecification;

/**
 * Implemention of {@link org.apache.tapestry.engine.IEngineService} used
 * as part of the banner ad framework.
 * 
 * @author Howard Lewis Ship
 */
public class BannerService extends AbstractService
{
    public static final String SERVICE_NAME = "bannerads.banner";

    /**
     *  The lone parameter should be the banner id.
     */
    public ILink getLink(IRequestCycle cycle, IComponent component, Object[] parameters)
    {
        return constructLink(cycle, SERVICE_NAME, null, parameters, false);
    }

    public void service(
        IEngineServiceView engine,
        IRequestCycle cycle,
        ResponseOutputStream output)
        throws ServletException, IOException
    {
        IApplicationSpecification specification = engine.getSpecification();
        IBannerSource source =
            (IBannerSource) specification.getExtension(
                IBannerSource.BANNER_SOURCE_EXTENSION_NAME,
                IBannerSource.class);

        source.initialize(cycle);

        Object[] parameters = getParameters(cycle);

        String bannerId = (String) parameters[0];

        IBanner banner = source.getBanner(bannerId);
        source.recordClickThru(bannerId);

        throw new RedirectException(banner.getClickThruURL());
    }

    public String getName()
    {
        return SERVICE_NAME;
    }

}
