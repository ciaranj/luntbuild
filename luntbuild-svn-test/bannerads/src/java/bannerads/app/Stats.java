/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package bannerads.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.spec.IApplicationSpecification;

import bannerads.library.IBanner;
import bannerads.library.IBannerSource;

/**
 * Page used to display impression and click-thru statistics for banners displayed
 * on the site.
 * 
 * @author Howard Lewis Ship
 */
public class Stats extends BasePage
{
    /**
     * Returns the banners, sorted by id.
     */
    public List getSortedBanners()
    {
        IApplicationSpecification spec = getEngine().getSpecification();
        IBannerSource source =
            (IBannerSource) spec.getExtension(
                IBannerSource.BANNER_SOURCE_EXTENSION_NAME,
                IBannerSource.class);

        source.initialize(getRequestCycle());

        List ids = source.getBannerIds();

        Collections.sort(ids);

        int count = ids.size();
        List result = new ArrayList(count);

        for (int i = 0; i < count; i++)
        {
            String id = (String) ids.get(i);
            IBanner banner = source.getBanner(id);
            result.add(banner);
        }

        return result;
    }
}
