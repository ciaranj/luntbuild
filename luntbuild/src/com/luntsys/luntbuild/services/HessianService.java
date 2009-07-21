/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-19
 * Time: 15:48:20
 *
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package com.luntsys.luntbuild.services;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.server.HessianSkeleton;
import com.luntsys.luntbuild.facades.ILuntbuild;
import com.luntsys.luntbuild.utility.Luntbuild;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.AbstractService;
import org.apache.tapestry.engine.IEngineServiceView;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.request.ResponseOutputStream;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A service for accessing the Luntbuild system directly. It will internally call the Luntbuild service.
 * 
 * @author robin shine
 * @see ILuntbuild
 */
public class HessianService extends AbstractService {
	private static Log logger = LogFactory.getLog(HessianService.class);

	/** Name of this service, used to access this service. */
	public static final String SERVICE_NAME = "hessian";

	/**
     * Builds a link.
     * 
     * <p>A single parameter is expected, the resource path of the asset
     * (which is expected to start with a leading slash).</p>
     * 
	 * @param cycle the request cycle being processed
	 * @param component the component requesting the URL
	 * @param parameters additional parameters specific to the component
	 * @return the URL
	 */
	public ILink getLink(IRequestCycle cycle, IComponent component, Object[] parameters) {
		return constructLink(cycle, SERVICE_NAME, null, null, false);
	}

	/**
	 * Connects the request output stream to the Luntbuild service.
	 * 
	 * @param engine a view of the {@link org.apache.tapestry.IEngine} with additional methods needed by services
	 * @param cycle the incoming request
	 * @param output stream to which output should ultimately be directed
	 * @throws ServletException if an exception happens while servicing the request
	 * @throws IOException not thrown
	 * @see ILuntbuild
	 */
	public void service(IEngineServiceView engine, IRequestCycle cycle, ResponseOutputStream output) throws ServletException, IOException {
		try {
			ILuntbuild luntbuildService = Luntbuild.getLuntbuildService();
			HessianSkeleton serviceInvoker = new HessianSkeleton(luntbuildService, ILuntbuild.class);
			InputStream in = cycle.getRequestContext().getRequest().getInputStream();
			OutputStream out = cycle.getRequestContext().getResponse().getOutputStream();
			serviceInvoker.invoke(new HessianInput(in), new HessianOutput(out));
		} catch (Throwable throwable) {
			logger.error("Exception caught in HessianService.service()", throwable);
			throw new ServletException(throwable);
		}
	}

	/**
	 * Gets the name of this service.
	 * 
	 * @return the service name
	 */
	public String getName() {
		return SERVICE_NAME;
	}
}
