/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-4-30
 * Time: 8:36:29
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

import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.spec.IComponentSpecification;

/**
 * this page is used to show aspects of a specific project.
 *
 * @author robin shine
 */
public abstract class ProjectPage extends HierarchyPage {
	public static final long SERVICE_PARAMETER_SCHEDULES = 1;
	private Project project;

	public String getPageDataDescription() {
		if (getProjectId() == 0)
			return "project - creating...";
		else {
			return "project - " + getProject().getName();
		}
	}

	public void detach() {
		super.detach();
		project = null;
	}

	public void finishLoad(IRequestCycle cycle, IPageLoader loader, IComponentSpecification specification) {
		super.finishLoad(cycle, loader, specification);
		project = null;
	}

	public PageInfo[] getHierarchyPageInfos() {
		PageInfo[] pageInfos = new PageInfo[1];
		pageInfos[0] = new PageInfo();
		pageInfos[0].setPageName("Home");

		// data description and id are non-sense for home page, we are just make some
		// initialization of them
		pageInfos[0].setPageDataDesciption("Home");
		pageInfos[0].setPageDataId(Home.SERVICE_PARAMETER_HOME);

		return pageInfos;
	}

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		assignProjectId(((Long)parameters[0]).longValue());
		if (parameters.length >= 2) {
			long actionId = ((Long) parameters[1]).longValue();
			if (actionId == SERVICE_PARAMETER_SCHEDULES) {
				TabControl tabs = (TabControl) getComponent("tabs");
				tabs.setSelectedTabId("schedules");
			}
		}
	}

	public void setProject(Project project) {
		this.project = project;
		if (project != null)
			assignProjectId(project.getId());
	}

	public Project getProject() {
		if (project == null) {
			if (getProjectId() != 0)
				project = Luntbuild.getDao().loadProject(getProjectId());
			else
				project = new Project();
		}
		return project;
	}

	public void saveProject() {
		Luntbuild.getDao().saveProject(getProject());
		// sync project id in case of new project saving
		setProjectId(getProject().getId());
	}

	public abstract long getProjectId();

	public abstract void setProjectId(long projectId);

	public void assignProjectId(long projectId) {
		TabControl tabs = (TabControl) getComponent("tabs");
		tabs.setSelectedTabId("basic");
		ProjectBasicTab basicTab = (ProjectBasicTab) getComponent("basic");
		if (projectId == 0)
			basicTab.setAction("editProject");
		else
			basicTab.setAction(null);
		setProjectId(projectId);
	}

	public int getRefreshInterval(){
		TabControl tabs = (TabControl) getComponent("tabs");
		if (tabs.getSelectedTabId().equals("schedules")) {
			return ((SchedulesTab) tabs.getSelectedTab()).getRefreshInterval();
		} else
			return 0;
	}
}
