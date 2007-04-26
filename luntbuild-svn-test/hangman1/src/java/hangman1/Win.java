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
 *  Page displayed when the player wins ... succesfully guesses
 *  all the letters in the word.
 *
 *  @author Howard Lewis Ship
 */

public class Win extends BasePage
{
    private char _letter;

    /**
     *  This method must return the page back to its pristine state.
     * 
     **/

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

    public String getLetterLabel()
    {
        char upper = Character.toUpperCase(_letter);

        return new Character(upper).toString();
    }

    public IAsset getLetterImage()
    {
        if (_letter == '_')
            return getAsset("dash");

        return getAsset(new Character(_letter).toString());
    }
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
