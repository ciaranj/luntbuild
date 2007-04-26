/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package bannerads.library.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;

import org.apache.commons.digester.Digester;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.IResourceLocation;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.resource.ContextResourceLocation;

import bannerads.library.IBanner;
import bannerads.library.IBannerSource;

/**
 * Implementation of {@link bannerads.library.IBannerSource} that reads
 * banner information from an XML file.  Click-thru and impression counts
 * are stored in the instance, but not persisted anywhere.
 * 
 * @author Howard Lewis Ship
 */
public class XMLBannerSource implements IBannerSource
{
    private Random random = new Random();

    private String _bannersFile = "banners.xml";

    /**
     * Map of {@link Banner} keyed on (String) banner id.
     */
    private Map _banners;
    private List _bannerIds;

    public IBanner getRandomBanner()
    {
        int count = _bannerIds.size();
        int index = random.nextInt(count);
        String id = (String) _bannerIds.get(index);

        return getBanner(id);
    }

    public IBanner getBanner(String bannerId)
    {
        return (IBanner) _banners.get(bannerId);
    }

    public void recordImpression(String bannerId)
    {
        Banner b = (Banner) getBanner(bannerId);
        b.incrementImpressions();

    }

    public void recordClickThru(String bannerId)
    {
        Banner b = (Banner) getBanner(bannerId);
        b.incrementClickThrus();
    }

    /**
     * Initializes the list of banners from the a file
     * stored in WEB-INF.
     */
    public synchronized void initialize(IRequestCycle cycle)
    {
        if (_banners == null)
        {
            ServletContext servletContext =
                cycle.getRequestContext().getServlet().getServletContext();
            IResourceLocation location =
                new ContextResourceLocation(servletContext, "/WEB-INF/" + _bannersFile);

            Banner[] banners = read(location);

            _banners = new HashMap();
            _bannerIds = new ArrayList();

            for (int i = 0; i < banners.length; i++)
            {
                String id = banners[i].getId();
                _bannerIds.add(id);
                _banners.put(id, banners[i]);
            }
        }
    }

    public String getBannersFile()
    {
        return _bannersFile;
    }

    public void setBannersFile(String string)
    {
        _bannersFile = string;
    }

    public Banner[] read(IResourceLocation location)
    {
        Digester digester = new Digester();

        digester.addObjectCreate("banner-ads", ArrayList.class);
        digester.addObjectCreate("banner-ads/banner", Banner.class);

        // The attribute names match the property names, so this can do it all.
        digester.addSetProperties("banner-ads/banner");

        // This adds each banner instance to the list, by
        // invoking method add() on the ArrayList.

        digester.addSetNext("banner-ads/banner", "add");

        URL fileURL = location.getResourceURL();
        
        List banners = null;
        try
        {

            InputStream stream = fileURL.openStream();

            banners = (List) digester.parse(stream);

            stream.close();
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException("Unable to read banner ads file " + location, ex);
        }

        int count = Tapestry.size(banners);

        return (Banner[]) banners.toArray(new Banner[count]);
    }

    public List getBannerIds()
    {
        return new ArrayList(_bannerIds);
    }

}
