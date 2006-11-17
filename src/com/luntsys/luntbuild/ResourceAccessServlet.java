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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * This servlet provides access to public resource such as documents.
 *
 * @author robin shine
 */
public class ResourceAccessServlet extends HttpServlet {
	private static Log logger = LogFactory.getLog(ResourceAccessServlet.class);

	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		if (httpServletRequest.getServletPath().startsWith("/docs")) {
			String pathToRequest = (String) httpServletRequest.getPathInfo();
			String docsDir = Luntbuild.installDir + File.separator + "docs";
			File fileToRequest = new File(docsDir + File.separator + pathToRequest);
			String pathRelativeToPublishDir = Luntbuild.parseRelativePath(new File(docsDir), fileToRequest);
			if (pathRelativeToPublishDir == null || pathRelativeToPublishDir.equals("")) // requested file equals to docs directory or is not under docs directory
				throw new ServletException("Invalid file requested: " + fileToRequest.getAbsolutePath());
			if (fileToRequest.isFile())
				Luntbuild.sendFile(httpServletRequest, httpServletResponse, fileToRequest.getAbsolutePath());
			else
				throw new ServletException("Invalid file requested: " + fileToRequest.getAbsolutePath());
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
		} else {
			throw new ServletException("ResourceAccessServlet rejects servlet path: " + httpServletRequest.getServletPath());
		}
	}
}
