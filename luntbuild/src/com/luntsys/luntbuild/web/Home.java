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
package com.luntsys.luntbuild.web;

import com.luntsys.luntbuild.BuildGenerator;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IRequestCycle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The start page for a tapestry web application
 *
 * @author robin shine
 */
public class Home extends HierarchyPage {
	/**
	 * Constants to identify meaning of the first service param if that
	 * value is negative.
	 */
	public static final long SERVICE_PARAMETER_HOME = 0;
	public static final long SERVICE_PARAMETER_BUILD = 1;
	public static final long SERVICE_PARAMETER_BUILDLOG = 2;
	public static final long SERVICE_PARAMETER_REVISIONLOG = 3;
	public static final long SERVICE_PARAMETER_SYSTEMLOG = 4;

	public PageInfo[] getHierarchyPageInfos() {
		return new PageInfo[0];
	}

	public String getPageDataDescription() {
		return "Home";
	}

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		TabControl tabs = (TabControl) getComponent("tabs");
		if (parameters == null) { // activated by first enter home page, or by restart session, or page refresh
			TabPageComponent selectedTab = (TabPageComponent) tabs.getSelectedTab();
			if (!(selectedTab instanceof BuildsTab))
				selectedTab.tabSelected(); // notify selected tab
		} else {
			long actionCode = ((Long) parameters[0]).longValue();
			if (actionCode == SERVICE_PARAMETER_HOME) { // activated by clicking the Home link in the navigation area
				TabPageComponent selectedTab = (TabPageComponent) tabs.getSelectedTab();
				selectedTab.tabSelected();
			} else if (actionCode == SERVICE_PARAMETER_SYSTEMLOG) {
				Luntbuild.sendFile(cycle, Luntbuild.installDir + "/logs/" + Luntbuild.log4jFileName);
			} else {
				if (actionCode == SERVICE_PARAMETER_BUILD) { // try to show a specific build
					tabs.setSelectedTabId("builds");
					BuildsTab buildsTab = (BuildsTab) tabs.getSelectedTab();
					cycle.setServiceParameters(new Long[]{(Long) parameters[1]});
					buildsTab.viewBuild(cycle);
				} else {
					long buildId = ((Long) parameters[1]).longValue();
					Build build = Luntbuild.getDao().loadBuild(buildId);
					String publishDir = build.getPublishDir();
					if (actionCode == SERVICE_PARAMETER_BUILDLOG) // try to download the build log file
						Luntbuild.sendFile(cycle, publishDir + "/" + BuildGenerator.BUILD_LOG);
					else // try to download the revision log file
						Luntbuild.sendFile(cycle, publishDir + "/" + BuildGenerator.REVISION_LOG);
				}
			}
		}
	}

	public void pageActivated(IRequestCycle cycle) {
		if (cycle.getRequestContext().getRequest().getPathInfo() == null) // request to show home page
			activateExternalPage(cycle.getServiceParameters(), cycle);
		else { // request a particular artifacts file
			if (cycle.getRequestContext().getRequest().getServletPath().startsWith("/statefulPublish")) {
				TabControl tabs = (TabControl) getComponent("tabs");
				BuildsTab buildsTab = (BuildsTab) tabs.getTab("builds");
				BuildViewer buildViewer = (BuildViewer) buildsTab.getComponent("buildViewerComponent");
				cycle.setServiceParameters(new String[]{cycle.getRequestContext().getRequest().getPathInfo()});
				buildViewer.requestFile(cycle);
			} else if (cycle.getRequestContext().getRequest().getServletPath().startsWith("/docs")) { // requesting help documents
				String pathToRequest = (String) cycle.getRequestContext().getRequest().getPathInfo();
				String docsDir = Luntbuild.installDir + File.separator + "docs";
				File fileToRequest = new File(docsDir + File.separator + pathToRequest);
				String pathRelativeToPublishDir = Luntbuild.parseRelativePath(new File(docsDir), fileToRequest);
				if (pathRelativeToPublishDir == null || pathRelativeToPublishDir.equals("")) // requested file equals to docs directory or is not under docs directory
					throw new ApplicationRuntimeException("Invalid file requested!");
				if (fileToRequest.isFile())
					Luntbuild.sendFile(cycle, fileToRequest.getAbsolutePath());
				else
					throw new ApplicationRuntimeException("Invalid file requested!");
			} else { // requesting publishing artifacts
				String fields[] = cycle.getRequestContext().getRequest().getPathInfo().split("/");
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
					build = Luntbuild.getDao().loadLastBuild(projectName, scheduleName);
				else if (buildVersion.equalsIgnoreCase(com.luntsys.luntbuild.facades.Constants.LAST_BUILD))
					build = Luntbuild.getDao().loadLastBuild(projectName, scheduleName);
				else
					build = Luntbuild.getDao().loadBuild(projectName, scheduleName, buildVersion);

				String pathToRequest = build.getPublishDir();
				for (int i = 3; i < pathElements.size(); i++) {
					pathToRequest += File.separator;
					pathToRequest += pathElements.get(i);
				}
				Luntbuild.sendFile(cycle, pathToRequest);
			}
		}
	}

	public int getRefreshInterval() {
		TabControl tabs = (TabControl) getComponent("tabs");
		if (tabs.getSelectedTabId().equals("builds")) {
			return ((BuildsTab) tabs.getSelectedTab()).getRefreshInterval();
		} else
			return 0;
	}
}