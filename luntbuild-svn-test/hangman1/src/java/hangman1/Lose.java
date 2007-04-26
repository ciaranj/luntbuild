/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package hangman1;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;

/**
 *
 *  Page displayed when the player misses too many guesses.
 *  Displays the target word, and gives the player a chance
 *  to start a new game.
 *
 *  @author Howard Lewis Ship
 */

public class Lose extends BasePage
{
    private char _letter;

    public void initialize()
    {
        _letter = 0;
    }

    public char getLetter()
    {
        return _letter;
    }

    public void setLetter(char letter)
    {
        _letter = letter;
    }

    public char getLetterLabel()
    {
        return Character.toUpperCase(_letter);
    }

    public IAsset getLetterImage()
    {
        String name = new Character(_letter).toString();

        return getAsset(name);
    }

    /**
     *  Listener method; invokes 
     *  {@link hangman1.Visit#startGame(IRequestCycle)}.
     * 
     **/

    public void playAgain(IRequestCycle cycle)
    {
        Visit visit = (Visit) getVisit();

        visit.startGame(cycle);
    }
}
