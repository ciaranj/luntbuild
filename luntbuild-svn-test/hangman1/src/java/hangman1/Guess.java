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
 *  A page that presents the state of the game to the user and allows the user to 
 *  make guesses (by clicking on images for different letters).
 *
 *  @author Howard Lewis Ship
 */

public class Guess extends BasePage
{
    private char _letter;
    private boolean _letterGuessed;
    private int _guessIndex;

    /**
     *  This method must return the page back to its pristine state.
     * 
     **/

    public void initialize()
    {
        _letter = 0;
        _letterGuessed = false;
        _guessIndex = 0;
    }

    public char getLetter()
    {
        return _letter;
    }

    public void setLetter(char letter)
    {
        _letter = letter;
    }

    /**
     *  The {@link Game} stores the letters of the target word as lower case,
     *  but the UI looks better if the labels on the images are in upper case.
     * 
     **/

    public String getLetterLabel()
    {
        return ("" + _letter).toUpperCase();
    }

    /**
     *  Returns the image for the current letter (which may be an underscore
     *  as well).
     * 
     **/

    public IAsset getLetterImage()
    {
        if (_letter == '_')
            return getAsset("dash");

        return getAsset("" + _letter);
    }

    /**
     *  Returns true if the letter corresponding to the
     *  current {@link #getGuessIndex() guess index}
     *  has already been guessed by the user.
     * 
     **/

    public boolean isLetterGuessed()
    {
        return _letterGuessed;
    }

    /**
     *  Returns the current guess index, a number beteween 0
     *  and 25 which represents the letter to be guessed.
     *
     **/

    public int getGuessIndex()
    {
        return _guessIndex;
    }

    public void setLetterGuessed(boolean letterGuessed)
    {
        _letterGuessed = letterGuessed;
    }

    public void setGuessIndex(int guessIndex)
    {
        _guessIndex = guessIndex;
    }

    /**
     *  Return the image to display for the current
     *  {@link #getGuessIndex() guess index}, either a blank
     *  space if the letter has already been guessed, or
     *  the image for the corresponding letter.
     * 
     **/

    public IAsset getGuessImage()
    {
        if (_letterGuessed)
            return getAsset("space");

        String name = "" + getLetterForGuessIndex();

        return getAsset(name);
    }

    /**
     *  Returns the letter corresponding to the
     *  current {@link #getGuessIndex() guess index}
     *  as a letter between 'a' and 'z'.
     * 
     **/

    public char getLetterForGuessIndex()
    {
        return (char) ('a' + _guessIndex);
    }

    /**
     *  Returns the label for the guess image, either a space,
     *  or an upper-case letter.
     * 
     **/

    public String getGuessLabel()
    {
        if (_letterGuessed)
            return " ";

        char ch = Character.toUpperCase(getLetterForGuessIndex());

        return new Character(ch).toString();
    }

    /**
     *  Listener method for the select link component.  We define the parameter
     *  to be the character to guess.
     * 
     **/

    public void makeGuess(IRequestCycle cycle)
    {
        // Java wraps the char as an instance of Character
        Object[] parameters = cycle.getServiceParameters();
        Character guess = (Character) parameters[0];

        char ch = guess.charValue();

        // Get the Visit and cast it to our application-specific
        // class.

        Visit visit = (Visit) getVisit();

        visit.makeGuess(cycle, ch);
    }
}
