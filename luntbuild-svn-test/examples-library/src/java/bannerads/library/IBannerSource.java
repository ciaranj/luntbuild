/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package bannerads.library;

import java.util.List;

import org.apache.tapestry.IRequestCycle;

/**
 * Interface for a service which provides {@link bannerads.library.IBanner} instances, and
 * tracks impressions and click-thrus for those instances.
 * 
 * @author Howard Lewis Ship
 */
public interface IBannerSource
{
	/**
	 * Name ("<code>bannerads.banner-source</code>")
	 * of an application extension that implements
	 * {@link bannerads.IBannerSource}. 
	 */
	public static final String BANNER_SOURCE_EXTENSION_NAME = "bannerads.banner-source";
	
    /**
     * Invoked to ensure the source is initialized. This
     * must always be invoked before any other methods.
     * 
     */

    public void initialize(IRequestCycle cycle);

    /**
     * Gets a random banner instance.
     */
    public IBanner getRandomBanner();

    /**
     * Gets a specific banner instance.
     */
    public IBanner getBanner(String bannerId);

    /**
     * Records an impression (that is, notes that a particular
     * banner was displayed on a page.
     */
    public void recordImpression(String bannerId);

    /**
     * Records a clickthru, indicating the user clicked on
     * the banner ad and was redirected to the banner site.
     */
    public void recordClickThru(String bannerId);
    
    /**
     * Returns the ids of all banners as a List of
     * String.  The List is not sorted, but may
     * be freely modified by the caller.
     */  
    public List getBannerIds();
}
