/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package bannerads.library.components;

import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.IEngine;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.spec.IApplicationSpecification;

import bannerads.library.BannerService;
import bannerads.library.IBanner;
import bannerads.library.IBannerSource;

/**
 * Renders a &lt;a&gt; tag around a &lt;img&gt; tag; this consistitues a
 * banner impression.
 * 
 * @author Howard Lewis Ship
 */
public class BannerAd extends AbstractComponent
{

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        if (cycle.isRewinding())
            return;

        IBanner banner = getRandomBanner(cycle);
        IEngineService service = cycle.getEngine().getService(BannerService.SERVICE_NAME);

        ILink link = service.getLink(cycle, this, new Object[] { banner.getId()});

        writer.begin("a");
        writer.attribute("href", link.getURL());
        renderInformalParameters(writer, cycle);

        writer.beginEmpty("img");
        writer.attribute("src", banner.getImageURL());
        writer.attribute("width", banner.getWidth());
        writer.attribute("height", banner.getHeight());
        writer.attribute("alt", banner.getTitle());
        writer.attribute("border", 0);

        writer.end(); // <a>
    }

    private IBanner getRandomBanner(IRequestCycle cycle)
    {
        IEngine engine = cycle.getEngine();
        IApplicationSpecification specification = engine.getSpecification();
        IBannerSource source =
            (IBannerSource) specification.getExtension(
                IBannerSource.BANNER_SOURCE_EXTENSION_NAME,
                IBannerSource.class);

        source.initialize(cycle);

        IBanner result = source.getRandomBanner();

        source.recordImpression(result.getId());

        return result;
    }

}
