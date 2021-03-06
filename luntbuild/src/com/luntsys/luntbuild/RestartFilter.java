/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-7-2
 * Time: 21:06:36
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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.acegisecurity.ui.rememberme.TokenBasedRememberMeServices;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caucho.hessian.io.HessianOutput;
import com.luntsys.luntbuild.facades.LuntbuildException;
import com.luntsys.luntbuild.services.HessianService;
import com.luntsys.luntbuild.utility.Luntbuild;

/**
 * Servlet filter that handles restarts and sets encoding to UTF-8.
 *
 * @author lubosp
 */
public class RestartFilter implements Filter {
	private static Log logger = LogFactory.getLog(RestartFilter.class);

	/**
	 * Initializes filter.
	 * 
	 * @param filterConfig the config information
	 * @throws ServletException not thrown
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	/**
	 * Filters requests by invalidating any requests that occur while the Luntbuild service
	 * is restarting.
	 * 
	 * @param servletRequest the request object
	 * @param servletResponse the response object
	 * @param filterChain a chain of additional filters to apply
	 * @throws ServletException if detected when handling the request
	 * @throws IOException if detected when handling the request
	 * @throws RuntimeException if detected when handling the request
	 */
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {
		try  {
    		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            servletRequest.setCharacterEncoding("UTF-8");
            servletResponse.setContentType( "text/html; charset=UTF-8" );
    		String service = httpServletRequest.getParameter("service");
    		String relogin = httpServletRequest.getParameter("relogin");

    		// Tapestry restart service have no effect on Orion. Invalidate session here again.
    		if (service != null && service.equals("restart")) {
                if (relogin == null || !relogin.equalsIgnoreCase("no")) {
                    String url = httpServletResponse.encodeRedirectURL("luntbuild-login.html");
                    httpServletResponse.sendRedirect(url);
                } else {
                    filterChain.doFilter(servletRequest, servletResponse);
                }
            } else if(service != null && service.startsWith("reset")) {
                // Invalidate session
                HttpSession session = httpServletRequest.getSession(false);
                if (session != null) session.invalidate();
                // Invalidate remember me cookie
                Cookie terminate =
                    new Cookie(TokenBasedRememberMeServices.ACEGI_SECURITY_HASHED_REMEMBER_ME_COOKIE_KEY,
                            null);
                terminate.setMaxAge(0);
                httpServletResponse.addCookie(terminate);
                if (relogin == null || !relogin.equalsIgnoreCase("no")) {
                    String url = httpServletResponse.encodeRedirectURL("luntbuild-login.html");
                    httpServletResponse.sendRedirect(url);
                } else {
                    filterChain.doFilter(servletRequest, servletResponse);
                }
    		} else
    		    filterChain.doFilter(servletRequest, servletResponse);
		} catch (Exception e) {
			logger.error("Exception occurred while processing http request.", e);

			HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
			HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
			if (httpRequest.getParameter("service") != null &&
                    httpRequest.getParameter("service").trim().equals(HessianService.SERVICE_NAME)) {
				// Request is initialized by hessian client, we should fill in response carefully
                // to make it compatible with hessian protocol
				try {
					LuntbuildException ex = new LuntbuildException(Luntbuild.getExceptionMessage(e));
					HessianOutput out = new HessianOutput(httpResponse.getOutputStream());
					out.startReply();
					out.writeFault("ServiceException", ex.getMessage(), ex);
					out.completeReply();
				} catch (Throwable throwable) {
					logger.error("Exception catched while filling hessian response: ", throwable);
					throw new ServletException(throwable);
				}
			} else {
				if (e instanceof IOException)
					throw (IOException) e;
				if (e instanceof ServletException)
					throw (ServletException) e;
				if (e instanceof RuntimeException)
					throw (RuntimeException) e;
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Destroys this filter.
	 */
	public void destroy() {
	}
}
