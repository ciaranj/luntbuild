/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-3-15
 * Time: 19:58:11
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

package com.luntsys.luntbuild;

import org.acegisecurity.AccessDeniedException;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.engine.BaseEngine;
import org.apache.tapestry.request.ResponseOutputStream;
import org.apache.tapestry.request.RequestContext;

import javax.servlet.ServletException;

/**
 * Customized engine to do some extra initialization work before handling a request.
 */
public class LuntbuildEngine extends BaseEngine {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @inheritDoc
	 */
	protected void setupForRequest(RequestContext requestContext) {
		super.setupForRequest(requestContext);
	}

	/**
	 * Handles access denied exceptions.  All other exceptions are handled by
	 * {@link BaseEngine#activateExceptionPage(IRequestCycle, ResponseOutputStream, Throwable)}.
	 * 
	 * @throws ServletException if unable to render the exception page
	 * @see org.acegisecurity.AccessDeniedException
	 */
	protected void activateExceptionPage(IRequestCycle cycle, ResponseOutputStream output, Throwable cause) throws ServletException {
		if (cause instanceof AccessDeniedException || cause.getCause() != null && cause.getCause() instanceof AccessDeniedException) {
			try
			{
				IPage exceptionPage = cycle.getPage("AccessDeniedExceptionPage");

				cycle.activate(exceptionPage);

				renderResponse(cycle, output);
			}
			catch (Throwable ex)
			{
				// Worst case scenario.  The exception page itself is broken, leaving
				// us with no option but to write the cause to the output.

				reportException(
					Tapestry.getMessage("AbstractEngine.unable-to-process-client-request"),
					cause);

				// Also, write the exception thrown when redendering the exception
				// page, so that can get fixed as well.

				reportException(
					Tapestry.getMessage("AbstractEngine.unable-to-present-exception-page"),
					ex);

				// And throw the exception.

				throw new ServletException(ex.getMessage(), ex);
			}
		} else {
			super.activateExceptionPage(cycle, output, cause);
		}
	}
}
