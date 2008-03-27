/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-1-24
 * Time: 14:00:15
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

package com.luntsys.luntbuild.security;

import com.caucho.hessian.io.HessianOutput;
import com.luntsys.luntbuild.services.HessianService;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.ui.AuthenticationEntryPoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class is provided to replace {@link org.acegisecurity.ui.basicauth.BasicProcessingFilterEntryPoint}. The reason is to
 * make authentication failure response compatible with hessian protocol in case request is initiated by a hessian client.
 * 
 * @author robin shine
 */
public class BasicProcessingFilterEntryPoint implements AuthenticationEntryPoint {
	private static Log logger = LogFactory.getLog(BasicProcessingFilterEntryPoint.class);

    /**
     * @inheritDoc
     */
    public void commence(final ServletRequest request, final ServletResponse response, final AuthenticationException exception)
			throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
		if (httpRequest.getParameter("service") != null && httpRequest.getParameter("service").trim().equals(HessianService.SERVICE_NAME)) {
			// Request is initialized by hessian client, we should fill in response carefully to make it compatible with hessian protocol
			try {
			    final com.luntsys.luntbuild.facades.LuntbuildException ex = new com.luntsys.luntbuild.facades.LuntbuildException("Authentication failed!");
			    final HessianOutput out = new HessianOutput(httpResponse.getOutputStream());
				out.startReply();
				out.writeFault("ServiceException", ex.getMessage(), ex);
				out.completeReply();
				return;
			} catch (Throwable throwable) {
				logger.error("Exception caught while filling hessian response: ", throwable);
				throw new ServletException(throwable);
			}
		} else {
			httpResponse.addHeader("WWW-Authenticate",
					"Basic realm=\"luntbuild\"");
			httpResponse.sendError(401);
		}
	}
}
