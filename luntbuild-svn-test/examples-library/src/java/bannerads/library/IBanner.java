/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package bannerads.library;

/**
 * Interface that defines a banner ad. Instances of this interface
 * are provided by a {@link bannerads.library.IBannerSource}.
 * 
 * @author Howard Lewis Ship
 */
public interface IBanner
{
    /**
     * Returns the unique identifier for this banner ad, used to track
     * impressions and click-thrus.
     */
    public String getId();

    /**
     * Returns the URL for the banner image.
     */

    public String getImageURL();

    /**
     * Returns the width of the image.
     */

    public int getWidth();

    /**
     * Returns the height of the image.
     */

    public int getHeight();

    /**
     * Returns the click-thru URL for the banner ad.
     */
    public String getClickThruURL();

    /**
     * Returns the title (alt text) for the banner.
     * 
     */
    public String getTitle();
}
