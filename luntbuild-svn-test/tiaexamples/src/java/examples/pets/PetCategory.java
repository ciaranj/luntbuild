/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.pets;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.html.BasePage;

public abstract class PetCategory extends BasePage
{
	public abstract void setType(String type);
	public abstract String getType();
	
	public String getPageTitle()
	{
		return getMessages().getMessage("title." + getType());
	}
	
	public IAsset getImage()
	{
		return getAsset(getType());
	}
	
	public String getTypeName()
	{
		return getMessages().getMessage(getType());
	}
}
