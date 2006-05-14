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

public class AssetService extends org.apache.tapestry.asset.AssetService
{
    /**
     * @see org.apache.tapestry.engine.IEngineService#getLink(org.apache.tapestry.IRequestCycle, org.apache.tapestry.IComponent, java.lang.Object[])
     */
    public ILink getLink(IRequestCycle requestCycle, IComponent component, Object[] parameters)
    {
        if (Tapestry.size(parameters) != 1)
        {
            throw new ApplicationRuntimeException(Tapestry.format("service-single-parameter", Tapestry.ASSET_SERVICE));
        }

        return new AssetLink(requestCycle, "asset", (String) parameters[0]);
    }

	public void service(IEngineServiceView engine, IRequestCycle cycle, ResponseOutputStream output) throws ServletException, IOException {
		throw new RuntimeException("Not implemented!");
	}
}
