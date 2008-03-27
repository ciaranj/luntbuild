/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-18
 * Time: 10:31:54
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

import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.db.Build;
import org.apache.tapestry.ApplicationRuntimeException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

/**
 * Servlet to handle requests for public resources such as documents.
 *
 * @author robin shine
 */
public class ResourceAccessServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -819260279056874433L;
	
	private static final String LUNTBUILD_BASE_URL = "http://luntbuild.javaforge.com/";
	private static final String DOCS_GUIDE_URL = LUNTBUILD_BASE_URL + "manual/guide/manual.html";
	private static final String DOCS_QUICKSTART_URL = LUNTBUILD_BASE_URL + "doc/quickstart/quickstart.html";
	private static final String DOCS_FAQ_URL = LUNTBUILD_BASE_URL + "doc/faq/index.html";
	private static final String DOCS_JAVADOC_URL = LUNTBUILD_BASE_URL + "doc/javadoc/index.html";
	private static final String DOCS_API_URL = LUNTBUILD_BASE_URL + "remote-api/javadoc/index.html";

	/**
	 * Handles a requests.
	 * 
	 * @param httpServletRequest the HTTP request object
	 * @param httpServletResponse the HTTP response object
	 * @throws ServletException if the request could not be handled
	 * @throws IOException if detected when handling the request
	 */
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		if (httpServletRequest.getServletPath().startsWith("/docs")) {
			String pathToRequest = httpServletRequest.getPathInfo();
			String docsDir = Luntbuild.installDir + File.separator + "docs";
			File fileToRequest = new File(docsDir + File.separator + pathToRequest);
			String pathRelativeToPublishDir = Luntbuild.parseRelativePath(new File(docsDir), fileToRequest);
			if (pathRelativeToPublishDir == null || pathRelativeToPublishDir.equals("")) // requested file equals to docs directory or is not under docs directory
				httpServletResponse.sendRedirect(getDocsUrl(pathToRequest));
			else if (fileToRequest.isFile())
				Luntbuild.sendFile(httpServletRequest, httpServletResponse, fileToRequest.getAbsolutePath());
			else
				httpServletResponse.sendRedirect(getDocsUrl(pathToRequest));
		} else if (httpServletRequest.getServletPath().startsWith("/logs")) {
			Luntbuild.sendFile(httpServletRequest, httpServletResponse, Luntbuild.installDir + "/logs/" + Luntbuild.log4jFileName);
		} else if (httpServletRequest.getServletPath().startsWith("/publish")) {
			String fields[] = httpServletRequest.getPathInfo().split("/");
			List pathElements = new ArrayList();
			for (int i = 0; i < fields.length; i++) {
				String field = fields[i];
				if (!Luntbuild.isEmpty(field))
					pathElements.add(field.trim());
			}
			if (pathElements.size() < 3)
				throw new ApplicationRuntimeException("Requested page does not exist!");

			String projectName = (String) pathElements.get(0);
			String scheduleName = (String) pathElements.get(1);
			String buildVersion = (String) pathElements.get(2);

			Build build;
			if (buildVersion.equalsIgnoreCase(com.luntsys.luntbuild.facades.Constants.LAST_SUCCESS_BUILD))
				build = Luntbuild.getDao().loadLastBuildInternal(projectName, scheduleName);
			else if (buildVersion.equalsIgnoreCase(com.luntsys.luntbuild.facades.Constants.LAST_BUILD))
				build = Luntbuild.getDao().loadLastBuildInternal(projectName, scheduleName);
			else
				build = Luntbuild.getDao().loadBuildInternal(projectName, scheduleName, buildVersion);

			String pathToRequest = build.getPublishDir();
			for (int i = 3; i < pathElements.size(); i++) {
				pathToRequest += File.separator;
				pathToRequest += pathElements.get(i);
			}
			Luntbuild.sendFile(httpServletRequest, httpServletResponse, pathToRequest);
		} else if (httpServletRequest.getServletPath().startsWith("/asset")){
			String assetLocation = httpServletRequest.getParameter("location");
			assetLocation = assetLocation.substring(1);
			if (assetLocation.startsWith("/"))
				assetLocation = assetLocation.substring(1);
			URL assetUrl = Thread.currentThread().getContextClassLoader().getResource(assetLocation);
			Luntbuild.sendFile(httpServletRequest, httpServletResponse, assetUrl);
		} else if (httpServletRequest.getServletPath().endsWith(".xsl")) {
			String pathToRequest = httpServletRequest.getServletPath();
			File fileToRequest = new File(Luntbuild.installDir + File.separator + pathToRequest);
			if (fileToRequest.isFile())
				Luntbuild.sendFile(httpServletRequest, httpServletResponse, fileToRequest.getAbsolutePath());
			else
				throw new ServletException("Invalid file requested: " + fileToRequest.getAbsolutePath());
		} else {
			throw new ServletException("ResourceAccessServlet rejects servlet path: " + httpServletRequest.getServletPath());
		}
	}
	
	private String getDocsUrl(String path) {
		if (path == null || path.trim().length() == 0) return DOCS_GUIDE_URL;
		if (path.indexOf("guide") >= 0) return DOCS_GUIDE_URL;
		if (path.indexOf("quickstart") >= 0) return DOCS_QUICKSTART_URL;
		if (path.indexOf("faq") >= 0) return DOCS_FAQ_URL;
		if (path.indexOf("javadoc") >= 0) return DOCS_JAVADOC_URL;
		if (path.indexOf("api") >= 0) return DOCS_API_URL;
		
		return DOCS_GUIDE_URL;
	}
}
