/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package bannerads.library.impl;

import bannerads.library.IBanner;

/**
 * Basic implementation of {@link bannerads.library.IBanner}.
 * 
 * @author Howard Lewis Ship
 */
public class Banner implements IBanner
{
    private String _id;
    private String _imageURL;
    private int _width;
    private int _height;
    private String _clickThruURL;
    private String _title;
    private int _impressions;
    private int _clickThrus;

    public String getClickThruURL()
    {
        return _clickThruURL;
    }

    public int getHeight()
    {
        return _height;
    }

    public String getId()
    {
        return _id;
    }

    public String getImageURL()
    {
        return _imageURL;
    }

    public String getTitle()
    {
        return _title;
    }

    public int getWidth()
    {
        return _width;
    }

    public void setClickThruURL(String string)
    {
        _clickThruURL = string;
    }

    public void setHeight(int height)
    {
        _height = height;
    }

    public void setId(String string)
    {
        _id = string;
    }

    public void setImageURL(String string)
    {
        _imageURL = string;
    }

    public void setTitle(String string)
    {
        _title = string;
    }

    public void setWidth(int width)
    {
        _width = width;
    }

	public int getClickThrus()
	{
		return _clickThrus;
	}
	
	public int getImpressions()
	{
		return _impressions;
	}
	
	public void incrementClickThrus()
	{
		_clickThrus++;
	}
	
	public void incrementImpressions()
	{
		_impressions++;
	}
}
