/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package hangman1;

import org.apache.tapestry.IRequestCycle;

/**
 *
 *  The Visit class runs most of the game logic and acts as controller, mediating
 *  between the pure interface code and the pure logic code.
 *
 *  @author Howard Lewis Ship
 */

public class Visit
{
    // In a real application, the word source would be shared between all sessions.  
    // Here, we just allow each Visit to have its own instance.

    private WordSource _wordSource = new WordSource();

    // On the other hand, the Game is specifically for this
    // Visit and only this Visit.

    private Game _game = new Game();

    public void startGame(IRequestCycle cycle)
    {
        _game.start(_wordSource.nextWord());

        // Now that the Game is initialized, we can go to the Guess 
        // page to allow the player to start making guesses.

        cycle.activate("Guess");
    }

    /**
     *  Processes the player's guess, possibly updating the response
     *  page to be "Win" or "Lose".
     * 
     **/

    public void makeGuess(IRequestCycle cycle, char ch)
    {
        // If this return true, then stay on this page at let
        // player keep guessing.

        if (_game.makeGuess(ch))
            return;

        cycle.activate(_game.isWin() ? "Win" : "Lose");
    }

    /**
     *  Returns the {@link Game} instance for this Visit; this is used
     *  primarily by the {@link Guess} page to display things like
     *  the number of remaining guesses and the list of guessed
     *  and unguessed letters.
     * 
     **/

    public Game getGame()
    {
        return _game;
    }
}
