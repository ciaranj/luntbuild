/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package hangman2;

import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

/**
 * Component used to display an image for a particular letter (used when displaying 
 * the partially guessed word, and in the letter grid the user selects from).
 * 
 * @author Howard Lewis Ship
 */
public abstract class Letter extends AbstractComponent
{
    public abstract boolean isDisabled();
    public abstract char getLetter();

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        writer.beginEmpty("img");

        writer.attribute("src", getLetterImage().buildURL(cycle));
        writer.attribute("alt", getLetterLabel());
        writer.attribute("height", 36);
        writer.attribute("width", 36);
        writer.attribute("border", 0);
    }

    public IAsset getLetterImage()
    {
        if (isDisabled())
            return getAsset("space");

        char letter = getLetter();

        if (letter == '_')
            return getAsset("dash");

        return getAsset("" + letter);
    }

    public String getLetterLabel()
    {
        if (isDisabled())
            return " ";

        return ("" + getLetter()).toUpperCase();
    }

}
