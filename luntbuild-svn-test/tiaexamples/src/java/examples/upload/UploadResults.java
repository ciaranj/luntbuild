/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.util.io.BinaryDumpOutputStream;

/**
 * Page activated to show the content of an uploaded file.
 * 
 * @author Howard Lewis Ship
 */
public abstract class UploadResults extends BasePage
{
    public abstract void setFile(IUploadFile file);
    public abstract IUploadFile getFile();

    private static class ContentRenderer implements IRender
    {
        private IUploadFile _file;

        ContentRenderer(IUploadFile file)
        {
            _file = file;
        }

        public void render(IMarkupWriter writer, IRequestCycle cycle)
        {
            try
            {
                StringWriter buffer = new StringWriter();
                BinaryDumpOutputStream out = new BinaryDumpOutputStream(buffer);

                out.setBytesPerLine(32);
                out.setShowAscii(true);

                InputStream in = _file.getStream();

                copy(in, out);

                in.close();
                out.close();

                writer.print(buffer.getBuffer().toString());

            }
            catch (IOException ex)
            {
                throw new ApplicationRuntimeException("Unable to generate binary output.", ex);
            }
        }

        private void copy(InputStream in, OutputStream out) throws IOException
        {
            byte[] buffer = new byte[1000];

            while (true)
            {
                int length = in.read(buffer);

                if (length < 0)
                    return;

                out.write(buffer, 0, length);
            }
        }

    }

    public IRender getContentRenderer()
    {
        return new ContentRenderer(getFile());
    }
}
