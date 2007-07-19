package com.luntsys.luntbuild.services;

import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.request.ResponseOutputStream;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.engine.IEngineServiceView;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * A service for building URLs to assets. Most of the work is deferred to <code>AssetLink</link>.
 * 
 * @see AssetLink
 */
public class AssetService extends org.apache.tapestry.asset.AssetService
{
    /**
     * Builds a link for an asset.
     * 
     * <p>A single parameter is expected, the resource path of the asset
     * (which is expected to start with a leading slash).</p>
     * 
     * @param requestCycle the request cycle being processed
     * @param component the component requesting the URL
     * @param parameters additional parameters specific to the component
     * @return the URL for the asset
     * @see AssetLink
     */
    public ILink getLink(IRequestCycle requestCycle, IComponent component, Object[] parameters)
    {
        if (Tapestry.size(parameters) != 1)
        {
            throw new ApplicationRuntimeException(Tapestry.format("service-single-parameter", "asset.as"));
        }

        return new AssetLink(requestCycle, "asset.as", (String) parameters[0]);
    }

	/**
	 * Retrieves a resource from the classpath and returns it to the client in a binary output stream.
	 * 
	 * <p>Note: this method is not implemented.</p>
	 * 
	 * @param engine a view of the {@link org.apache.tapestry.IEngine} with additional methods needed by services
	 * @param cycle the incoming request
	 * @param output stream to which output should ultimately be directed
	 * @throws ServletException not thrown
	 * @throws IOException not thrown
	 * @throws RuntimeException because this is not implemented
	 */
	public void service(IEngineServiceView engine, IRequestCycle cycle, ResponseOutputStream output) throws ServletException, IOException {
		throw new RuntimeException("Not implemented!");
	}
}
