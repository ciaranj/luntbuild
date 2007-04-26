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
 *  Used with both the Win and the Lose page, simply to
 *  provide a place for the playAgain listener method.
 *
 *  @author Howard Lewis Ship
 */

public class WinLose extends BasePage
{

    /**
     *  Listener method; invokes {@link hangman1.Visit#startGame(IRequestCycle)}.
     * 
     **/

    public void playAgain(IRequestCycle cycle)
    {
        Visit visit = (Visit) getVisit();

        visit.startGame(cycle);
    }

}
