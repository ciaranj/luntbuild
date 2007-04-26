/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package hangman2;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;

/**
 *
 *  This class contains the logic for the Home page; currently, that's very straight forward, a 
 *  <em>listener method</em> that is invoked when the user clicks the
 *  start button on the page.
 *
 *  @author Howard Lewis Ship
 */

public class Home extends BasePage
{
	public void start(IRequestCycle cycle)
	{
		// Get the visit object and cast it to the application-specific class, Visit.
		
		Visit visit = (Visit)getVisit();
		
		visit.startGame(cycle);
	}
}
