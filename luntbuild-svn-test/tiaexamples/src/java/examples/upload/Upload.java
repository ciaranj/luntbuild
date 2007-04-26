/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.upload;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.request.IUploadFile;

/**
 * Page used to demonstrate the use of the Upload component.
 * 
 * @author Howard Lewis Ship
 */
public abstract class Upload extends BasePage
{
    public abstract IUploadFile getFile();

    public void formSubmit(IRequestCycle cycle)
    {
        IUploadFile file = getFile();

        if (file == null)
            return;

        UploadResults next = (UploadResults) cycle.getPage("UploadResults");
        next.setFile(file);
        cycle.activate(next);
    }
}
