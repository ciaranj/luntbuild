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
 *  A page that presents the state of the game to the user and allows the user to 
 *  make guesses (by clicking on images for different letters).
 *
 *  @author Howard Lewis Ship 
 */

public abstract class Guess extends BasePage
{

    public abstract char getLetter();

    /**
     *  Returns true if the letter corresponding to the
     *  current {@link #getGuessIndex() guess index}
     *  has already been guessed by the user.
     * 
     **/

    public abstract boolean isLetterGuessed();

    /**
     *  Returns the current guess index, a number beteween 0
     *  and 25 which represents the letter to be guessed.
     *
     **/

    public abstract int getGuessIndex();

    /**
     *  Returns the letter corresponding to the
     *  current {@link #getGuessIndex() guess index}
     *  as a letter between 'a' and 'z'.
     * 
     **/

    public char getLetterForGuessIndex()
    {
        return (char) ('a' + getGuessIndex());
    }

    /**
     *  Listener method for the select link component.  We define the parameter
     *  to be the character to guess.
     * 
     **/

    public void makeGuess(IRequestCycle cycle)
    {
        // Java wraps the char as an instance of Character

        Character guess = (Character) cycle.getServiceParameters()[0];

        char ch = guess.charValue();

        // Get the Visit and cast it to our application-specific
        // class.

        Visit visit = (Visit) getVisit();

        visit.makeGuess(cycle, ch);
    }
}
